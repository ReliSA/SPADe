package cz.zcu.kiv.spade.gui.tabs;

import cz.zcu.kiv.spade.gui.SPADeGUI;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
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
        sendPost();
    }



    private void sendPost() {

        //HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        List<NameValuePair> urlParameters = new ArrayList<>();

        StringBuilder content = new StringBuilder();
        BufferedReader reader;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(folder + prjSelect.getSelectionModel().getSelectedItem())
                            , StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        urlParameters.add(new BasicNameValuePair("graph", content.toString()));

        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //webEngine.load(client.execute(post).getEntity());
    }
}
