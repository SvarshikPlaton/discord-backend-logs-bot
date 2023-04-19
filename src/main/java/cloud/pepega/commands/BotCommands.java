package cloud.pepega.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class BotCommands extends ListenerAdapter {
    private String resolveInversionOption(SlashCommandInteractionEvent event) {
        var inversionOption = event.getOption("inversion");
        String inversion = "";
        if (inversionOption == null) {
            inversion = "-r";
        } else if (inversionOption.getAsBoolean()) {
            inversion = "-r";
        }
        return inversion;
    }

    private String resolveDatetimeOption(SlashCommandInteractionEvent event) {
        var datetimeOption = event.getOption("datetime");
        String datetime = "";
        if (datetimeOption != null) {
            datetime = "--since \"" + datetimeOption.getAsString() + "\"";
        }
        return datetime;
    }

    private int resolveRowCountOption(SlashCommandInteractionEvent event) {
        var rowCountOption = event.getOption("rowcount");
        int rowCount = 1000;
        if (rowCountOption != null) {
            rowCount = rowCountOption.getAsInt();
            if (rowCount < 50) {
                rowCount = 50;
            }
        }
        return rowCount;
    }

    private void sendLogsFile(String service, String inversion, String datetime, int rowCount,
                              SlashCommandInteractionEvent event) {
        try {
            var pb = new ProcessBuilder(
                    "bash", "-c", "journalctl -u " +
                    service + " " +
                    inversion + " " +
                    datetime);
            var process = pb.start();

            System.out.println(String.join(" ",pb.command().toArray(new String[0])));

            var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            var byteBuffer = new ByteArrayOutputStream();

            String line;
            for (int i = 0; i < rowCount; i++) {
                if ((line = reader.readLine()) != null && byteBuffer.size() < 20_000_000) {
                    byteBuffer.write(line.getBytes());
                    byteBuffer.write('\n');
                }
                else
                    break;
            }

            System.out.println(byteBuffer.size());

            var file = FileUpload.fromData(byteBuffer.toByteArray(), "logs.txt");
            event.replyFiles(file).queue();
        } catch (IOException e) {
            event.reply("Something went wrong when invoking the command..").queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "logs" -> {

                var eventOption = event.getOption("service-name");
                String service;
                if (eventOption == null) {
                    event.reply("Invalid service name..").queue();
                    return;
                } else {
                    service = eventOption.getAsString();
                }

                var inversion = resolveInversionOption(event);
                var datetime = resolveDatetimeOption(event);
                var rowcount = resolveRowCountOption(event);

                sendLogsFile(service, inversion, datetime, rowcount, event);
            }
            case "polishcat" -> {
                try {
                    var file = new File("/var/lib/jenkins/polishcat.mp3");
                    event.replyFiles(FileUpload.fromData(file)).queue();
                } catch (Exception e) {
                    event.reply("Song not found :frowning:").queue();
                }
            }
        }
    }
}
