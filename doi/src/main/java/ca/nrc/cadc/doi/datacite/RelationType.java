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
*  with OpenCADC.  If not, see          OpenCADC ; si ce n’esties(serverNode);

            // return the node in xml format
            NodeWriter nodeWriter = new NodeWriter();
            return new NodeActionResult(new N
*  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
*                                       <http://www.gnu.org/licenses/>.
*
*  $Revision: 4 $
*
************************************************************************
*/

package ca.nrc.cadc.doi.datacite;

/**
 * Description of the relationship of the resource being registered (A) and the related resource (B).
 * 
 * @author yeunga
 *
 */
public enum RelationType {
    ARK("ARK"), 

    IS_CITED_BY("IsCitedBy"), 
    CITES("Cites"), 
    IS_SUPPLEMENT_TO("IsSupplementTo"), 
    IS_SUPPLEMENTED_BY("IsSupplementedBy"), 
    IS_CONTINUED_BY("IsContinuedBy"), 
    CONTINUES("Continues"), 
    IS_NEW_VERSION_OF("IsNewVersionOf"), 
    IS_PREVIOUS_VERSION_OF("IsPreviousVersionOf"), 
    IS_PART_OF("IsPartOf"), 
    HAS_PART("HasPart"), 
    IS_REFERENCED_BY("IsReferencedBy"),
    REFERENCES("References"), 
    IS_DOCUMENTED_BY("IsDocumentedBy"), 
    DOCUMENTS("Documents"), 
    IS_COMPILED_BY("IsCompiledBy"), 
    COMPILES("Compiles"), 
    IS_VARIANT_FORM_OF("IsVariantFormOf"), 
    IS_ORIGINAL_FORM_OF("IsOriginalFormOf"), 
    IS_IDENTICAL_TO("IsIdenticalTo"), 
    HAS_METADAT("HasMetadata"), 
    IS_METADATA_FOR("IsMetadataFor"), 
    REVIEWS("Reviews"), 
    IS_REVIEWED_BY("IsReviewedBy"), 
    IS_DERIVED_FROM("IsDerivedFrom"), 
    IS_SOURCE_OF("IsSourceOf"), 
    DESCRIBES("Describes"),
    IS_DESCRIBED_BY("IsDescribedBy"), 
    HAS_VERSION("HasVersion"), 
    IS_VERSION_OF("IsVersionOf"),
    REQUIRES("Requires"), 
    IS_REQUIRED_BY("IsRequiredBy"); 

    private final String value;

    private RelationType(String value) {
        this.value = value;
    }

    public static RelationType toValue(String s) {
        for (RelationType type : values())
            if (type.value.equals(s))
                return type;
        throw new IllegalArgumentException("invalid value: " + s);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + value + "]";
    }
}