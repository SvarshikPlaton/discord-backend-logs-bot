package pepega.cloud.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class BotCommands extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "logs" -> {

                OptionMapping service = event.getOption("service-name");
                if (service == null) {
                    event.reply("Invalid service name..").queue();
                    return;
                }

                OptionMapping inversion = event.getOption("inversion");
                String inversionOption = "";
                if (inversion == null) {
                    inversionOption = "-r";
                } else if (inversion.getAsBoolean()) {
                    inversionOption = "-r";
                }

                OptionMapping datetime = event.getOption("datetime");
                String datetimeOption = "";
                if (datetime != null) {
                    datetimeOption = "--since \"" + datetime.getAsString() + "\"";
                }


                try {
//                    var pb = new ProcessBuilder(
//                        "journalctl",
//                        "-u", service.getAsString(),
//                        inversionOption,
//                        datetimeOption
//                    );

                    ProcessBuilder pb = new ProcessBuilder(
                            "bash", "-c", "journalctl -u " +
                            service.getAsString() + " " +
                            inversionOption + " " +
                            datetimeOption);
                    Process process = pb.start();

                    System.out.println(String.join(" ",pb.command().toArray(new String[0])));

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    var byteBuffer = new ByteArrayOutputStream();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        byteBuffer.write(line.getBytes());
                        byteBuffer.write('\n');
                    }

                    event.replyFiles(FileUpload.fromData(byteBuffer.toByteArray(), "logs.txt")).queue();
                } catch (IOException e) {
                    event.reply("Something went wrong when invoking the command..").queue();
                }
            }
            case "polishcat" -> {
                event.reply("Song!").queue();
            }
        }
    }
}
