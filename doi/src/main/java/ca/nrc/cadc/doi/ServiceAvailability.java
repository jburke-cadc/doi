/**
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2021.                            (c) 2021.
 *  Government of Canada                 Gouvernement du Canada
 *  National Research Council            Conseil national de recherches
 *  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 *  All rights reserved                  Tous droits réservés
 *
 *  NRC disclaims any warranties,        Le CNRC dénie toute garantie
 *  expressed, implied, or               énoncée, implicite ou légale,
 *  statutory, of any kind with          de quelque nature que ce
 *  respect to the software,             soit, concernant le logiciel,
 *  including without limitation         y compris sans restriction
 *  any warranty of merchantability      toute garantie de valeur
 *  or fitness for a particular          marchande ou de pertinence
 *  purpose. NRC shall not be            pour un usage particulier.
 *  liable in any event for any          Le CNRC ne pourra en aucun cas
 *  damages, whether direct or           être tenu responsable de tout
 *  indirect, special or general,        dommage, direct ou indirect,
 *  consequential or incidental,         particulier ou général,
 *  arising from the use of the          accessoire ou fortuit, résultant
 *  software.  Neither the name          de l'utilisation du logiciel. Ni
 *  of the National Research             le nom du Conseil National de
 *  Council of Canada nor the            Recherches du Canada ni les noms
 *  names of its contributors may        de ses  participants ne peuvent
 *  be used to endorse or promote        être utilisés pour approuver ou
 *  products derived from this           promouvoir les produits dérivés
 *  software without specific prior      de ce logiciel sans autorisation
 *  written permission.                  préalable et particulière
 *                                       par écrit.
 *
 *  This file is part of the             Ce fichier fait partie du projet
 *  OpenCADC project.                    OpenCADC.
 *
 *  OpenCADC is free software:           OpenCADC est un logiciel libre ;
 *  you can redistribute it and/or       vous pouvez le redistribuer ou le
 *  modify it under the terms of         modifier suivant les termes de
 *  the GNU Affero General Public        la “GNU Affero General Public
 *  License as published by the          License” telle que publiée
 *  Free Software Foundation,            par la Free Software Foundation
 *  either version 3 of the              : soit la version 3 de cette
 *  License, or (at your option)         licence, soit (à votre gré)
 *  any later version.                   toute version ultérieure.
 *
 *  OpenCADC is distributed in the       OpenCADC est distribué
 *  hope that it will be useful,         dans l’espoir qu’il vous
 *  but WITHOUT ANY WARRANTY;            sera utile, mais SANS AUCUNE
 *  without even the implied             GARANTIE : sans même la garantie
 *  warranty of MERCHANTABILITY          implicite de COMMERCIALISABILITÉ
 *  or FITNESS FOR A PARTICULAR          ni d’ADÉQUATION À UN OBJECTIF
 *  PURPOSE.  See the GNU Affero         PARTICULIER. Consultez la Licence
 *  General Public License for           Générale Publique GNU Affero
 *  more details.                        pour plus de détails.
 *
 *  You should have received             Vous devriez avoir reçu une
 *  a copy of the GNU Affero             copie de la Licence Générale
 *  General Public License along         Publique GNU Affero avec
 *  with OpenCADC.  If not, see          OpenCADC ; si ce n’est
 *  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
 *                                       <http://www.gnu.org/licenses/>.
 *
 ************************************************************************
 */
package ca.nrc.cadc.doi;

import ca.nrc.cadc.auth.AuthMethod;
import ca.nrc.cadc.net.HttpGet;
import ca.nrc.cadc.reg.Standards;
import ca.nrc.cadc.reg.client.LocalAuthority;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.vosi.AvailabilityPlugin;
import ca.nrc.cadc.vosi.Availability;
import ca.nrc.cadc.vosi.avail.CheckCertificate;
import ca.nrc.cadc.vosi.avail.CheckResource;
import ca.nrc.cadc.vosi.avail.CheckWebService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.net.URL;

public class ServiceAvailability implements AvailabilityPlugin {
    
    private static URI VOS_URI = URI.create("ivo://cadc.nrc.ca/vault");
    private static String DATACITE_URL = "https://mds.datacite.org";
    private static File DOIADMIN_PEM_FILE = new File(System.getProperty("user.home") + "/.ssl/doiadmin.pem");
    private static File SERVOPS_PEM_FILE = new File(System.getProperty("user.home") + "/.ssl/cadcproxy.pem");

    public ServiceAvailability() {
    }

    @Override
    public void setAppName(String appName) {
        // no op
    }

    @Override
    public boolean heartbeat() {
        return true;
    }

    public Availability getStatus() {
        boolean isGood = true;
        String note = "service is accepting requests";
        try {
            // certificate needed to do any actions with VOSpace Client
            CheckCertificate checkCert = new CheckCertificate(DOIADMIN_PEM_FILE);
            checkCert.check();
            
            // certificate needed to do any A&A actions
            checkCert = new CheckCertificate(SERVOPS_PEM_FILE);
            checkCert.check();

            // check other services we depend on
            RegistryClient reg = new RegistryClient();
            URL url;
            CheckResource checkResource;
            
            LocalAuthority localAuthority = new LocalAuthority();

            URI credURI = localAuthority.getServiceURI(Standards.CRED_PROXY_10.toString());
            url = reg.getServiceURL(credURI, Standards.VOSI_AVAILABILITY, AuthMethod.ANON);
            checkResource = new CheckWebService(url);
            checkResource.check();

            URI usersURI = localAuthority.getServiceURI(Standards.UMS_USERS_01.toString());
            url = reg.getServiceURL(usersURI, Standards.VOSI_AVAILABILITY, AuthMethod.ANON);
            checkResource = new CheckWebService(url);
            checkResource.check();
            
            URI groupsURI = localAuthority.getServiceURI(Standards.GMS_SEARCH_01.toString());
            if (!groupsURI.equals(usersURI)) {
                url = reg.getServiceURL(groupsURI, Standards.VOSI_AVAILABILITY, AuthMethod.ANON);
                checkResource = new CheckWebService(url);
                checkResource.check();
            }
            
            url = reg.getServiceURL(VOS_URI, Standards.VOSI_AVAILABILITY, AuthMethod.ANON);
            checkResource = new CheckWebService(url);
            checkResource.check();

            // check that datacite is available
            URL docURL = new URL(DATACITE_URL);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            HttpGet get = new HttpGet(docURL, true);
            get.setHeadOnly(true);
            get.setConnectionTimeout(9000);
            get.setReadTimeout(9000);
            get.run();
            int responseCode = get.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("response code from " + DATACITE_URL + ": " + responseCode);
            }
        } catch (Throwable t) {
            // one of the status tests failed
            isGood = false;
            note = "test failed, reason: " + t;
        }
        return new Availability(isGood, note);
    }

    public void setState(String string) {
        // no-op
    }
}
