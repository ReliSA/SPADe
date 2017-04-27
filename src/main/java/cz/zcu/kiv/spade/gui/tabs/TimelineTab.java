package cz.zcu.kiv.spade.gui.tabs;

import cz.zcu.kiv.spade.gui.SPADeGUI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class TimelineTab extends BrowserTab {

    public TimelineTab(SPADeGUI gui) {
        super("Timeline", gui);

        url = "file:///C:/Users/oem/Downloads/pichy/_škola/_výzkum/_workspace/SPADe/Timeline/software/index.html";
        folder = "Timeline/software/data/";
        file = "data.js";

        webEngine.load(url);
    }

    @Override
    protected void selectProject() {
        try {
            Files.copy(Paths.get(folder + prjSelect.getSelectionModel().getSelectedItem()),
                    Paths.get(folder + file),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        webEngine.reload();
    }
}
