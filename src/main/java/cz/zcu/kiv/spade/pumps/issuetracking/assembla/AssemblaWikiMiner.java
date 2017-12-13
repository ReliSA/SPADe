package cz.zcu.kiv.spade.pumps.issuetracking.assembla;

import com.assembla.WikiPage;
import com.assembla.WikiPageVersion;
import com.assembla.client.AssemblaAPI;
import com.assembla.client.Paging;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.ArtifactClass;
import cz.zcu.kiv.spade.pumps.issuetracking.WikiMiner;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

class AssemblaWikiMiner extends WikiMiner {

    AssemblaWikiMiner(AssemblaPump pump) {
        super(pump);
    }

    @Override
    public void mineWiki() {
        Map<String, Artifact> wikies = new HashMap<>();
        // TODO explore Paging
        for (WikiPage wikiPage : ((AssemblaAPI) pump.getRootObject()).wikis(pump.getPi().getExternalId()).getAll(new Paging(0, -1)).asList()) {
            Artifact page = mineWikiPage(wikiPage);
            wikies.put(page.getExternalId(), page);
        }

        linkWikiPages(wikies);
    }

    private void linkWikiPages(Map<String, Artifact> wikies) {

        for (String childId : wikies.keySet()) {

            WikiPage childPage = ((AssemblaAPI) pump.getRootObject()).wikis(pump.getPi().getExternalId()).get(childId);

            Artifact parent = wikies.get(childPage.getParentId());
            Artifact child = wikies.get(childId);

            parent.getRelatedItems().add(new WorkItemRelation(child, resolveRelation(PARENT_OF)));
            child.getRelatedItems().add(new WorkItemRelation(parent, resolveRelation(CHILD_OF)));
        }
    }

    private Artifact mineWikiPage(WikiPage page) {
        Artifact artifact = new Artifact();
        artifact.setExternalId(page.getId());
        artifact.setArtifactClass(ArtifactClass.WIKIPAGE);
        artifact.setName(page.getPageName());
        artifact.setDescription(page.getContents());
        artifact.setAuthor(addPerson(((AssemblaPeopleMiner) pump.getPeopleMiner()).generateIdentity(page.getUserId())));

        // TODO check order of versions, now as if descending
        WikiPageVersion targetVersion = null;
        ZonedDateTime creation = page.getCreatedAt();

        for (WikiPageVersion version : ((AssemblaAPI) pump.getRootObject()).wikis(pump.getPi().getExternalId()).getVersions(page.getId(), new Paging(0, -1)).asList()) {
            if (targetVersion != null) {
                WorkItemChange change = new WorkItemChange();
                change.setExternalId(targetVersion.getId().toString());
                change.setChangedItem(artifact);
                if (version.getCreatedAt().equals(creation)){
                    change.setType(WorkItemChange.Type.MODIFY);
                    FieldChange fieldChange = new FieldChange(DESC_FIELD_NAME, version.getContents(), targetVersion.getContents());
                    change.getFieldChanges().add(fieldChange);
                } else {
                    change.setType(WorkItemChange.Type.ADD);
                }

                Configuration configuration = new Configuration();
                configuration.setCreated(((AssemblaPump) pump).convertDate(targetVersion.getCreatedAt()));
                configuration.setAuthor(addPerson(((AssemblaPeopleMiner) pump.getPeopleMiner()).generateIdentity(targetVersion.getUserId())));
                configuration.setDescription(targetVersion.getChangeComment());
                configuration.getChanges().add(change);
            }
            targetVersion = version;
        }

        return artifact;
    }
}
