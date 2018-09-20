/*
************************************************************************
*******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
**************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
*
*  (c) 2018.                            (c) 2018.
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

import ca.nrc.cadc.ac.Group;
import ca.nrc.cadc.ac.GroupURI;
import ca.nrc.cadc.ac.User;
import ca.nrc.cadc.ac.client.GMSClient;
import ca.nrc.cadc.auth.ACIdentityManager;

import ca.nrc.cadc.auth.SSLUtil;
import ca.nrc.cadc.doi.datacite.DoiXmlWriter;
import ca.nrc.cadc.doi.datacite.Resource;
import ca.nrc.cadc.net.OutputStreamWrapper;
import ca.nrc.cadc.net.ResourceNotFoundException;
import ca.nrc.cadc.vos.ContainerNode;
import ca.nrc.cadc.vos.DataNode;
import ca.nrc.cadc.vos.Direction;
import ca.nrc.cadc.vos.Node;
import ca.nrc.cadc.vos.NodeProperty;
import ca.nrc.cadc.vos.Protocol;
import ca.nrc.cadc.vos.Transfer;
import ca.nrc.cadc.vos.VOS;
import ca.nrc.cadc.vos.VOSURI;
import ca.nrc.cadc.vos.client.ClientTransfer;

import ca.nrc.cadc.vos.client.VOSpaceClient;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessControlException;
import java.security.Principal;
import java.security.PrivilegedExceptionAction;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.security.auth.Subject;
import org.apache.log4j.Logger;


public class PostAction extends DOIAction {
    private static final Logger log = Logger.getLogger(PostAction.class);

    // PostAction
    private static final String CREATE_REQUEST = "create";
    //    protected static final String EDIT_REQUEST = "edit";
    //    protected static final String MINT_REQUEST = "mint";

    private String requestType;  // from list above
    private String DOINumInputStr; // value used
    private Resource resource;
    private VOSpaceClient vosClient;
    protected List<NodeProperty> properties;

    private VOSURI target;

    public PostAction() {
        super();
    }

    @Override
    public void doAction() throws Exception {

        // Store calling user information for later reference
        setAuthorisedUser();

        // Discover what kind of request this is
        initRequest();

        // Get the submitted form data, if it exists
        resource = (Resource) syncInput.getContent(DoiInlineContentHandler.CONTENT_KEY);

        // Interact with VOSPACE using DOI_BASE_VOSPACE
        doiDataURI = new VOSURI(new URI(DOI_BASE_VOSPACE));

        // Do all subsequent work as doiadmin
        File pemFile = new File(System.getProperty("user.home") + "/.ssl/doiadmin.pem");
        Subject doiadminSubject = SSLUtil.createSubject(pemFile);
        Subject.doAs(doiadminSubject, new PrivilegedExceptionAction<Object>() {
            @Override
            public String run() throws Exception {
                // This should be done within the Subject for the user
                // that will be using the client.
                vosClient = new VOSpaceClient(doiDataURI.getServiceURI());
                doActionImpl();
                return "done";
            }
        });
    }

    private void doActionImpl() throws Exception {

        // DOINumInputStr is parsed out in DOIAction.initRequest()
        if (DOINumInputStr == null) {
            // Determine next DOI number
            Node baseNode = vosClient.getNode(doiDataURI.getPath());
            String nextDoiSuffix = generateNextDOINumber((ContainerNode)baseNode);
            log.debug("Next DOI suffix: " + nextDoiSuffix);

            // Create group to use for applying permissions
            GMSClient gmsClient = new GMSClient(new URI(GMS_URI_BASE));

            String doiGroupName = DOI_GROUP_PREFIX + nextDoiSuffix;
            String doiGroupURI = GMS_URI_BASE + "?" + doiGroupName;
            GroupURI guri = new GroupURI(new URI(doiGroupURI));

            Group doiRWGroup = new Group(guri);
            User member = new User();
            for (Principal p : callingSubject.getPrincipals()) {
                member.getIdentities().add(p);
            }
            doiRWGroup.getUserMembers().add(member);
            doiRWGroup.getUserAdmins().add(member);
            gmsClient.createGroup(doiRWGroup);

            log.debug("group uri made for " + nextDoiSuffix + ": " + doiGroupURI);

            properties = new ArrayList<>();

            // This will change to become public on minting. While in DRAFT,
            // directory is visible in AstroDataCitationDOI directory, but not readable
            // except by doiadmin and calling user's group
            NodeProperty isPublic = new NodeProperty(VOS.PROPERTY_URI_ISPUBLIC, "false");
            properties.add(isPublic);

            // Get numeric id for setting doiRequestor property
            ACIdentityManager acIdentMgr = new ACIdentityManager();
            Integer userNumericID = (Integer)acIdentMgr.toOwner(callingSubject);
            int n = (int)acIdentMgr.toOwner(callingSubject);

            // numeric id isn't parsing out correctly. getting only first digit?
            log.debug ("user numeric id on post POSTFOUND: " + userNumericID + " string version: " + userNumericID.toString());
            NodeProperty doiRequestor = new NodeProperty(DOI_REQUESTER_KEY, userNumericID.toString());
            log.debug("doi requester property value: " + doiRequestor.getPropertyValue());
            properties.add(doiRequestor);

            // All folders will be only readable by requester
            NodeProperty rGroup = new NodeProperty(VOS.PROPERTY_URI_GROUPREAD, doiGroupURI);
            properties.add(rGroup);


            // Create DOI containing folder using properties just set
            String folderName = getDoiNodeUri(nextDoiSuffix);
            String folderURI = getDoiNodeUri(nextDoiSuffix);
            target = new VOSURI(new URI(folderURI));
            Node newFolder = new ContainerNode(target, properties);
            vosClient.createNode(newFolder);


            // Go ahead and store the current DOI to the new directory
            // Update DOI xml with DOI number
            DOIAction.assignIdentifier(resource.getIdentifier(), CADC_DOI_PREFIX + "/" + nextDoiSuffix);
            // Create VOSpace data node to house XML doc using doi filename
            String nextDoiFilename = getDoiFilename(nextDoiSuffix);
            log.debug("next doi filename: " + nextDoiFilename);

            String doiFileUri = folderURI + "/" + nextDoiFilename;
            String doiFilename = folderName + "/" + nextDoiFilename;

            target = new VOSURI(new URI(doiFileUri));
            Node doiFileDataNode = new DataNode(target);
            vosClient.createNode(doiFileDataNode);

            postDoiDocToVospace(doiFilename);


            // Create 'data' folder under containing folder.
            // This is where the calling user will upload their DOI data
            // Will be writable by same group created before
            NodeProperty wGroup = new NodeProperty(VOS.PROPERTY_URI_GROUPWRITE,doiGroupURI);
            properties.add(wGroup);

            String dataFolderName = folderName + "/data";
            target = new VOSURI(new URI(dataFolderName));
            Node newDataFolder = new ContainerNode(target, properties);
            vosClient.createNode(newDataFolder);

            // Done, send redirect to GET for the XML file just made
            String redirectUrl = syncInput.getRequestURI() + "/" + nextDoiSuffix;
            syncOutput.setHeader("Location", redirectUrl);
            syncOutput.setCode(303);
        }
        else {
            throw new UnsupportedOperationException("Editing DOI Metadata not yet implemented.");
        }
    }

    private void postDoiDocToVospace (String dataNodeName)
        throws URISyntaxException, ResourceNotFoundException {
        // Upload document to named data node
        // Data node has already been created
        List<Protocol> protocols = new ArrayList<Protocol>();
        protocols.add(new Protocol(VOS.PROTOCOL_HTTP_PUT));
        Transfer transfer = new Transfer(new URI(dataNodeName), Direction.pushToVoSpace, protocols);
        ClientTransfer clientTransfer = vosClient.createTransfer(transfer);
        DoiOutputStream outStream = new DoiOutputStream(resource);
        clientTransfer.setOutputStreamWrapper(outStream);
        clientTransfer.run();

        if (clientTransfer.getThrowable() != null) {
            log.info(clientTransfer.getThrowable().getMessage());

            if (clientTransfer.getThrowable() != null) {
                log.debug(clientTransfer.getThrowable().getMessage());
                String message = clientTransfer.getThrowable().getMessage();
                if (message.contains("NodeNotFound")) {
                    throw new ResourceNotFoundException(message);
                }
                if (message.contains("PermissionDenied")) {
                    throw new AccessControlException(message);
                }
                throw new RuntimeException((clientTransfer.getThrowable().getMessage()));
            }

        }
    }

    private String generateNextDOINumber(ContainerNode baseNode) {
        // child nodes of baseNode should have name structure YY.XXXX
        // go through list of child nodes
        // extract XXXX
        // track largest
        // add 1
        // reconstruct YY.XXXX structure and return

        // Look into the node list for folders from current year only
        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String currentYear = df.format(Calendar.getInstance().getTime());

        Integer maxDoi = 0;
        if (baseNode.getNodes().size() > 0) {
            for( Node childNode : baseNode.getNodes()) {
                String[] nameParts = childNode.getName().split("\\.");
                if (nameParts[0].equals(currentYear)) {
                    int curDoiNum = Integer.parseInt(nameParts[1]);
                    if (curDoiNum > maxDoi) {
                        maxDoi = curDoiNum;
                    }
                }
            }
        }

        maxDoi++;
        String formattedDOI = String.format("%04d", maxDoi);
        return currentYear + "." + formattedDOI;
    }

    protected void initRequest() {
        String path = syncInput.getPath();
        log.debug("http request path: " + path);

        if (path == null) {
            return;
        }

        // Parse the request path to see if a DOI number has been provided
        String[] parts = path.split("/");
        if (parts.length > 0) {
            requestType = CREATE_REQUEST;
            DOINumInputStr = parts[0];
        }
        if (parts.length > 1) {
            throw new IllegalArgumentException("Invalid request: " + path);
        }
        log.debug("request type: " + requestType);
        log.debug("DOI Number: " + DOINumInputStr);
    }

    private class DoiOutputStream implements OutputStreamWrapper
    {
        private Resource resource;

        public DoiOutputStream(Resource resource)
        {
            this.resource = resource;
        }

        public void write(OutputStream out) throws IOException
        {
            DoiXmlWriter writer = new DoiXmlWriter();
            writer.write(resource, out);
        }
    }

}
