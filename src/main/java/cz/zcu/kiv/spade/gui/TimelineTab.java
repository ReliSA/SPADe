package cz.zcu.kiv.spade.gui;

import javafx.scene.control.Tab;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

class TimelineTab extends Tab {

    TimelineTab() {
        super("Timeline");

        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        webEngine.load("file:///C:/Users/oem/Downloads/pichy/_%C5%A1kola/_v%C3%BDzkum/_workspace/SPADe/Timeline/software/index.html");

        this.setContent(browser);
    }
}
