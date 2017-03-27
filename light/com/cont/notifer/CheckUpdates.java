package com.cont.notifer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cont.DownloadedNovelManager;
import com.cont.NovelData;
import com.cont.ReadLightNovelWebDownloader;
import com.cont.UpdateData;

public class CheckUpdates {
    private String os = System.getProperty("os.name").toLowerCase();
    private Path userHome = Paths.get(System.getProperty("user.home"));
    private static Path userPrivate = Paths.get(System.getProperty("user.home")).resolve("wbnov");
    private Path path = userPrivate.resolve("downloads");
    private long sleepTime = 1000 * 60 * 5;
    private Map<String, List<UpdateData>> updateMap = new HashMap<>();

    private void log(String message) {
        System.out.println("[" + new Date().toString() + "] " + message);
    }

    public void checkForUpdate(String... urls) {
        if (urls == null)
            return;
        int count = 1;
        System.setProperty("http.agent", "Chrome");
        String[] options = { "Open Chrome", "Download", "RemindLater", "MarkRead", "Re-Check",
                "Exit" };
        if (os.indexOf("mac") >= 0) {
            options[0] = "openBrowser";
        }

        while (true) {
            log("Initiate Check count " + count);
            boolean hasUpdate = false;
            List<String> updateUrls = new ArrayList<>();
            StringBuffer buffer = new StringBuffer();
            buffer.append("<html> <body>");
            for (String url : urls) {
                log("Checking URL :" + url);
                boolean update = dowork(ReadLightNovelWebDownloader.getUrlContents(url, true), url,
                        buffer);
                if (update) {
                    hasUpdate = true;
                    updateUrls.add(url);
                    log("Update available for :" + ReadLightNovelWebDownloader.getNameFromUrl(url));
                }
            }
            buffer.append("<br> </body></HTML>");
            if (hasUpdate) {
                // JOptionPane.showMessageDialog(null, buffer.toString(),
                // "Update Check ", JOptionPane.INFORMATION_MESSAGE);
                int opt = JOptionPane.showOptionDialog(null, buffer.toString(),
                        "Update available !! ", JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null, options, null);

                doSelectedOperation(opt, updateUrls, urls);
                // log(buffer.toString());
            } else {
                log("No updates..\n");
                sleep(sleepTime);
            }
            count++;
        }
    }

    private void doSelectedOperation(int value, List<String> updatedUrls, String... urls) {
        if (urls == null)
            return;
        for (String url : updatedUrls) {
            switch (value) {
            case 0:
                try {
                    String[] cmds = new String[] { "cmd", "/c", "start chrome " + url };
                    if (os.indexOf("mac") >= 0) {
                        cmds = new String[] { "open", url };
                    }
                    Runtime.getRuntime().exec(cmds);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            case 1:
                ReadLightNovelWebDownloader rlnd = new ReadLightNovelWebDownloader();
                rlnd.doWork(url, updateMap.get(url));
                break;
            case 2:
                log("Snooze after 6h");
                sleep(sleepTime * 12 * 6);
                break;
            case 3:
                log("Making urls...");
                ReadLightNovelWebDownloader rlnds = new ReadLightNovelWebDownloader();
                rlnds.doWork(url, updateMap.get(url), false);
                break;
            case 4:
                break;
            case 5:
                log("Exiting..");
                System.exit(0);
                break;
            default:
                sleep(sleepTime);
                break;
            }
        }
    }

    private void sleep(long sleepTime) {
        try {
            // log(buffer.toString());
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean dowork(String htmlString, String link, StringBuffer message) {
        String path = "";
        List<UpdateData> updates = new ArrayList<>();
        String name = ReadLightNovelWebDownloader.getNameFromUrl(link);
        Document document = Jsoup.parse(htmlString);
        Elements elements = document.getElementsByClass("tab-content");
        String section = "";
        String volName = "Vol";
        if (name != null && !name.equals("")) {
            path = this.path.toString() + File.separator + name + File.separator;
        }
        int volCount = 0;
        DownloadedNovelManager mgr = new DownloadedNovelManager();

        NovelData data = mgr.getNovelData(name, link);
        if (data == null) {
            String tmpLink = link;
            if (tmpLink.contains("http://")) {
                tmpLink = tmpLink.replace("http://", "https://");
            } else if (tmpLink.contains("https://")) {
                tmpLink = tmpLink.replace("https://", "http://");
            }
            data = mgr.getNovelData(name, tmpLink);
            if (data == null) {
                data = new NovelData();
                System.out.println("Assuming new Novel..");
            }
        }
        data.setUrlLink(link);
        data.setName(name);
        StringBuffer buffer = new StringBuffer();
        buffer.append(String.format(" <div id=\"%s\" class=\"%s\">", name, name));
        buffer.append("<center> <h3><u> " + name.toUpperCase() + "</u></h3> </center>");
        boolean update = false;
        String last = "[Unknown]";
        for (Element element : elements) {
            Elements pEleemnts = element.getElementsByClass("tab-pane");
            if (pEleemnts != null && pEleemnts.size() > 0) {
                String sec = pEleemnts.get(0).attr("id");
                if (sec != null && !section.equals(sec)) {
                    section = sec;
                    int sep = section.indexOf('-');
                    if (sep != -1) {
                        section = section.substring(0, sep);
                    }
                    volCount++;
                }
                Elements aElements = element.getElementsByTag("a");
                for (Element aElement : aElements) {
                    String contentLink = aElement.attr("href");
                    String value = aElement.text();
                    // System.out.println(String.format("Found download link for
                    // [%s], [%s],[%s] at link [%s]", name,
                    // volName + volCount, value, contentLink));
                    // data.addChapter(section, value);
                    String id = volName + volCount + "_"
                            + ReadLightNovelWebDownloader.makeId(value);
                    File file = new File(path + name + "_" + id + ".xhtml");
                    String tmpLn = contentLink;
                    if (tmpLn.contains("http://")) {
                        tmpLn = tmpLn.replace("http://", "https://");
                    } else if (tmpLn.contains("https://")) {
                        tmpLn = tmpLn.replace("https://", "http://");
                    }
                    if ((contentLink.trim().endsWith("/chapter-"))
                            || (data.isLinkDownloaded(contentLink) || data.isLinkDownloaded(tmpLn)
                                    || file.exists())) {
                        last = id;
                    } else {
                        if (!update)
                            buffer.append("<font color=\"red\">[New* unread*]</font>:");
                        updates.add(new UpdateData(name, link, contentLink, id,
                                file.getAbsolutePath()));
                        System.out.println(contentLink);
                        update = true;
                    }

                }
                buffer.append(this.makeUpdateDisplayString(updates));
            }
        }
        if (!update) {
            buffer.append("<font color=\"red\">[*NO UPDATES*]</font>");
        }
        buffer.append("<br><br><font color=\"green\"> Last Read :["
                + last.replace("_CH_", " Chapter ") + "]</font>");
        buffer.append("</div><br><hr>");
        if (update) {
            message.append(buffer.toString());
        }
        log("[Last : " + last.replace("_CH_", " Chapter ") + "] Updates:" + update);
        if (!updates.isEmpty()) {
            updateMap.put(link, updates);
        }
        return update;
    }

    private String makeUpdateDisplayString(List<UpdateData> updates) {
        StringBuffer buffer = new StringBuffer();
        int count = 0;
        if (updates != null && !updates.isEmpty()) {
            if (updates.size() > 12) {
                UpdateData first = updates.get(0);
                UpdateData last = updates.get(updates.size() - 1);
                buffer.append(String.format("<font color=\"blue\"><a href=\"%s\">%s</a></font>,",
                        last.getLinkUrl(), first.getId()));
                buffer.append(". . . . . . . ,");
                buffer.append(String.format("<font color=\"blue\"><a href=\"%s\">%s</a></font>",
                        last.getLinkUrl(), last.getId()));
                buffer.append(
                        String.format("<font color=\"red\">&nbsp;-&nbsp;[Total updates: %d]</font>",
                                updates.size()));
            } else {
                for (UpdateData update : updates) {
                    buffer.append(
                            String.format("<font color=\"blue\"><a href=\"%s\">%s</a></font>,",
                                    update.getLinkUrl(), update.getId()));
                    count++;
                    if (count % 8 == 0) {
                        buffer.append("<br>");
                    }
                }
            }
        }
        return buffer.toString();
    }

    public static void main(String args[]) {
        CheckUpdates check = new CheckUpdates();
        String urls[] = { "http://www.readlightnovel.com/zhan-long",
                "http://www.readlightnovel.com/tales-of-demons-and-gods",
                "http://www.readlightnovel.com/god-and-devil-world",
                "http://www.readlightnovel.com/against-the-gods",
                "http://www.readlightnovel.com/emperors-domination",
                "http://www.readlightnovel.com/martial-god-asura",
                "http://www.readlightnovel.com/dragon-marked-war-god" };
        check.checkForUpdate(urls);
    }
}
