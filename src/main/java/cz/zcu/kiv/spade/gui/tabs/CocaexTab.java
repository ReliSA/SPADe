package cz.zcu.kiv.spade.gui.tabs;

import cz.zcu.kiv.spade.gui.SPADeGUI;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CocaexTab extends BrowserTab {

    public CocaexTab(SPADeGUI gui) {
        super("Cocaex", gui);

        url = "http://relisa-dev.kiv.zcu.cz:8083/cocaex/ShowGraph";
        folder = "cocaex/";

        webEngine.load(url);
    }

    @Override
    public void refreshProjects(List<String> projects) {

    }

    @Override
    protected void selectProject() {
        try {
            sendPost();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void sendPost() throws IOException {

        /*HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        List<NameValuePair> urlParameters = new ArrayList<>();

        String content = "";
        BufferedReader reader;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(folder + prjSelect.getSelectionModel().getSelectedItem())
                            , StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                content += line.trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        urlParameters.add(new BasicNameValuePair("graph", content));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        webEngine.load(client.execute(post).getEntity());*/
    }
}
