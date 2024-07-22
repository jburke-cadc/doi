/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2024.                            (c) 2024.
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
 *  : 5 $
 *
 ************************************************************************
 */

package ca.nrc.cadc.doi;

import ca.nrc.cadc.doi.datacite.Affiliation;
import ca.nrc.cadc.doi.datacite.Contributor;
import ca.nrc.cadc.doi.datacite.ContributorName;
import ca.nrc.cadc.doi.datacite.ContributorType;
import ca.nrc.cadc.doi.datacite.Creator;
import ca.nrc.cadc.doi.datacite.CreatorName;
import ca.nrc.cadc.doi.datacite.Date;
import ca.nrc.cadc.doi.datacite.Description;
import ca.nrc.cadc.doi.datacite.ResourceType;
import ca.nrc.cadc.doi.datacite.Identifier;
import ca.nrc.cadc.doi.datacite.Language;
import ca.nrc.cadc.doi.datacite.NameIdentifier;
import ca.nrc.cadc.doi.datacite.PublicationYear;
import ca.nrc.cadc.doi.datacite.Publisher;
import ca.nrc.cadc.doi.datacite.RelatedIdentifier;
import ca.nrc.cadc.doi.datacite.Resource;
import ca.nrc.cadc.doi.datacite.Rights;
import ca.nrc.cadc.doi.datacite.Size;
import ca.nrc.cadc.doi.datacite.Title;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jdom2.Namespace;
import org.junit.Assert;

/**
 *
 */
public class BaseTest {

    List<Path> getTestFiles(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("path is null");
        }
        File testDir = new File(path);
        if (!testDir.isDirectory()) {
            throw new IllegalArgumentException(path + " is not a directory");
        }
        return Stream.of(testDir.listFiles()).filter(file -> !file.isDirectory()).map(File::toPath).collect(Collectors.toList());
    }

    void compareResource(Resource expected, Resource actual) {
        // required
        compareNamespace(expected.getNamespace(), actual.getNamespace());
        compareIdentifier(expected.getIdentifier(), actual.getIdentifier());
        compareCreators(expected.getCreators(), actual.getCreators());
        compareTitles(expected.getTitles(), actual.getTitles());
        comparePublisher(expected.getPublisher(), actual.getPublisher());
        comparePublicationYear(expected.getPublicationYear(), actual.getPublicationYear());
        compareDoiResourceType(expected.getResourceType(),actual.getResourceType());

        // optional
        compareContributors(expected.contributors, actual.contributors);
        compareDates(expected.dates, actual.dates);
        compareLanguage(expected.language, actual.language);
        compareRelatedIdentifiers(expected.relatedIdentifiers, actual.relatedIdentifiers);
        compareSizes(expected.sizes, actual.sizes);
        compareRightsList(expected.rightsList, actual.rightsList);
        compareDescriptions(expected.descriptions, actual.descriptions);
    }

    void compareNamespace(Namespace expected, Namespace actual) {
        Assert.assertNotNull("expected Namespace is null", expected);
        Assert.assertNotNull("actual Namespace is null", actual);
        Assert.assertEquals(expected.getPrefix(), actual.getPrefix());
        Assert.assertEquals(expected.getURI(), actual.getURI());
    }

    void compareIdentifier(Identifier expected, Identifier actual) {
        Assert.assertNotNull("expected Identifier is null", expected);
        Assert.assertNotNull("actual Identifier is null", actual);
        Assert.assertEquals(expected.getValue(), actual.getValue());
        Assert.assertEquals(expected.getIdentifierType(), actual.getIdentifierType());
    }

    void compareCreators(List<Creator> expected, List<Creator> actual) {
        Assert.assertNotNull("expected Creator's list is null", expected);
        Assert.assertNotNull("actual Creator's list is null", actual);
        Assert.assertFalse("expected Creator's list is empty", expected.isEmpty());
        Assert.assertFalse("actual Creator's list is empty", actual.isEmpty());
        Assert.assertEquals("list sizes are not equal", expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            compareCreator(expected.get(i), actual.get(i));
        }
    }

    void compareCreator(Creator expected, Creator actual) {
        Assert.assertNotNull("expected Creator is null", expected);
        Assert.assertNotNull("actual Creator is null", actual);
        compareCreatorName(expected.getCreatorName(), actual.getCreatorName());
        Assert.assertEquals(expected.givenName, actual.givenName);
        Assert.assertEquals(expected.familyName, actual.familyName);
        Assert.assertEquals(expected.lang, actual.lang);
        compareNameIdentifier(expected.nameIdentifier, actual.nameIdentifier);
        compareAffiliation(expected.affiliation, actual.affiliation);
    }

    void compareCreatorName(CreatorName expected, CreatorName actual) {
        Assert.assertNotNull("expected CreatorName is null", expected);
        Assert.assertNotNull("actual CreatorName is null", actual);
        Assert.assertEquals(expected.getValue(), actual.getValue());
        Assert.assertEquals(expected.nameType, actual.nameType);
        Assert.assertEquals(expected.lang, actual.lang);
    }

    void compareTitles(List<Title> expected, List<Title> actual) {
        Assert.assertNotNull("expected Title's is null", expected);
        Assert.assertNotNull("actual Title's is null", actual);
        Assert.assertFalse("expected Title's list is empty", expected.isEmpty());
        Assert.assertFalse("actual Title's list is empty", actual.isEmpty());
        Assert.assertEquals("list sizes are not equal", expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            compareTitle(expected.get(i), actual.get(i));
        }
    }

    void compareTitle(Title expected, Title actual) {
        Assert.assertEquals(expected.getValue(), actual.getValue());
        Assert.assertEquals(expected.titleType, actual.titleType);
        Assert.assertEquals(expected.lang, actual.lang);
    }

    void comparePublisher(Publisher expected, Publisher actual) {
        Assert.assertNotNull("expected Publisher is null", expected);
        Assert.assertNotNull("actual Publisher is null", actual);
        Assert.assertEquals(expected.getValue(), actual.getValue());
        Assert.assertEquals(expected.publisherIdentifier, actual.publisherIdentifier);
        Assert.assertEquals(expected.publisherIdentifierScheme, actual.publisherIdentifierScheme);
        Assert.assertEquals(expected.schemeURI, actual.schemeURI);
        Assert.assertEquals(expected.lang, actual.lang);
    }

    void comparePublicationYear(PublicationYear expected, PublicationYear actual) {
        Assert.assertNotNull("expected PublicationYear is null", expected);
        Assert.assertNotNull("actual PublicationYear is null", actual);
        Assert.assertEquals(expected.getValue(), actual.getValue());
    }

    void compareDoiResourceType(ResourceType expected, ResourceType actual) {
        Assert.assertNotNull("expected ResourceType is null", expected);
        Assert.assertNotNull("actual ResourceType is null", actual);
        Assert.assertEquals(expected.getResourceTypeGeneral(), actual.getResourceTypeGeneral());
        Assert.assertEquals(expected.text, actual.text);
    }

    void compareContributors(List<Contributor> expected, List<Contributor> actual) {
        if (isNull(expected, actual, "Dates")) {
            return;
        }
        Assert.assertEquals("list sizes are not equal", expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            compareContributor(expected.get(i), actual.get(i));
        }
    }

    void compareContributor(Contributor expected, Contributor actual) {
        Assert.assertNotNull("expected Contributor is null", expected);
        Assert.assertNotNull("actual Contributor is null", actual);
        compareContributorName(expected.getContributorName(), actual.getContributorName());
        compareContributorType(expected.getContributorType(), actual.getContributorType());
        Assert.assertEquals(expected.givenName, actual.givenName);
        Assert.assertEquals(expected.familyName, actual.familyName);
        compareNameIdentifier(expected.nameIdentifier, actual.nameIdentifier);
        compareAffiliation(expected.affiliation, actual.affiliation);
    }

    void compareContributorName(ContributorName expected, ContributorName actual) {
        Assert.assertNotNull("expected ContributorName is null", expected);
        Assert.assertNotNull("actual ContributorName is null", actual);
        Assert.assertEquals(expected.getValue(), actual.getValue());
        Assert.assertEquals(expected.nameType, actual.nameType);
        Assert.assertEquals(expected.lang, actual.lang);
    }

    void compareContributorType(ContributorType expected, ContributorType actual) {
        Assert.assertNotNull("expected ContributorType is null", expected);
        Assert.assertNotNull("actual ContributorType is null", actual);
        Assert.assertEquals(expected, actual);
    }

    void compareDates(List<Date> expected, List<Date> actual) {
        if (isNull(expected, actual, "Dates")) {
            return;
        }
        Assert.assertEquals("list sizes are not equal", expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            compareDate(expected.get(i), actual.get(i));
        }
    }

    void compareDate(Date expected, Date actual) {
        Assert.assertEquals(expected.getValue(), actual.getValue());
        Assert.assertEquals(expected.getDateType(), actual.getDateType());
        Assert.assertEquals(expected.dateInformation, actual.dateInformation);
    }

    void compareLanguage(Language expected, Language actual) {
        if (isNull(expected, actual, "RelatedIdentifiers")) {
            return;
        }
        Assert.assertEquals(expected.getValue(), actual.getValue());
    }

    void compareRelatedIdentifiers(List<RelatedIdentifier> expected, List<RelatedIdentifier> actual) {
        if (isNull(expected, actual, "RelatedIdentifiers")) {
            return;
        }
        Assert.assertEquals("list sizes are not equal", expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            compareRelatedIdentifier(expected.get(i), actual.get(i));
        }
    }

    void compareRelatedIdentifier(RelatedIdentifier expected, RelatedIdentifier actual) {
        Assert.assertEquals(expected.getValue(), actual.getValue());
        Assert.assertEquals(expected.getRelatedIdentifierType(), actual.getRelatedIdentifierType());
        Assert.assertEquals(expected.getRelationType(), actual.getRelationType());
        Assert.assertEquals(expected.dataCiteResourceTypeGeneral, actual.dataCiteResourceTypeGeneral);
        Assert.assertEquals(expected.relatedMetadataScheme, actual.relatedMetadataScheme);
        Assert.assertEquals(expected.schemeURI, actual.schemeURI);
        Assert.assertEquals(expected.schemeType, actual.schemeType);
    }

    void compareSizes(List<Size> expected, List<Size> actual) {
        if (isNull(expected, actual, "Sizes")) {
            return;
        }
        Assert.assertEquals("list sizes are not equal", expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            compareSize(expected.get(i), actual.get(i));
        }
    }

    void compareSize(Size expected, Size actual) {
        Assert.assertEquals(expected.getValue(), actual.getValue());
    }

    void compareRightsList(List<Rights> expected, List<Rights> actual) {
        if (isNull(expected, actual, "Rights")) {
            return;
        }
        Assert.assertEquals("list sizes are not equal", expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            compareRights(expected.get(i), actual.get(i));
        }
    }

    void compareRights(Rights expected, Rights actual) {
        Assert.assertEquals(expected.getValue(), actual.getValue());
        Assert.assertEquals(expected.rightsURI, actual.rightsURI);
        Assert.assertEquals(expected.rightsIdentifier, actual.rightsIdentifier);
        Assert.assertEquals(expected.rightsIdentifierScheme, actual.rightsIdentifierScheme);
        Assert.assertEquals(expected.schemeURI, actual.schemeURI);
        Assert.assertEquals(expected.lang, actual.lang);
    }

    void compareDescriptions(List<Description> expected, List<Description> actual) {
        if (isNull(expected, actual, "Descriptions")) {
            return;
        }
        Assert.assertEquals("list sizes are not equal", expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            compareDescription(expected.get(i), actual.get(i));
        }
    }

    void compareDescription(Description expected, Description actual) {
        Assert.assertEquals(expected.getValue(), actual.getValue());
        Assert.assertEquals(expected.getDescriptionType(), actual.getDescriptionType());
        Assert.assertEquals(expected.lang, actual.lang);
    }

    void compareNameIdentifier(NameIdentifier expected, NameIdentifier actual) {
        if (isNull(expected, actual, "NameIdentifier")) {
            return;
        }
        Assert.assertEquals(expected.getValue(), actual.getValue());
        Assert.assertEquals(expected.getNameIdentifierScheme(), actual.getNameIdentifierScheme());
        Assert.assertEquals(expected.schemeURI, actual.schemeURI);
    }

    void compareAffiliation(Affiliation expected, Affiliation actual) {
        if (isNull(expected, actual, "Affiliation")) {
            return;
        }
        Assert.assertEquals(expected.getValue(), actual.getValue());
        Assert.assertEquals(expected.affiliationIdentifier, actual.affiliationIdentifier);
        Assert.assertEquals(expected.affiliationIdentifierScheme, actual.affiliationIdentifierScheme);
        Assert.assertEquals(expected.schemeURI, actual.schemeURI);
    }

    boolean isNull(Object expected, Object actual, String name) {
        if (expected == null && actual == null) {
            return true;
        }
        if (expected == null) {
            Assert.fail(String.format("%s expected is null, actual: %s", name, actual));
        }
        if (actual == null) {
            Assert.fail(String.format("%s actual is null, expected: %s", name, expected));
        }
        return false;
    }

}