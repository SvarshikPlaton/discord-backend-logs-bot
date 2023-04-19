package cloud.pepega;

import cloud.pepega.commands.BotCommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;


public class DiscordBot {
    public static void main(String[] args) {
        JDA bot = JDABuilder.createDefault(System.getenv("DISCORD_BOT_TOKEN"))
                .setActivity(Activity.watching("your logs"))
                .addEventListeners(new BotCommands())
                .build();

        try {
            bot.awaitReady();
            Guild guild = bot.getGuildById(System.getenv("GUILD_ID"));

            if (guild != null) {
                guild.upsertCommand("logs", "Get logs from backend")
                        .addOption(OptionType.STRING, "service-name", "The name of your site. " +
                                "Options: provedcode / starlight / uptalentbackend / talantino / skillscope", true)
                        .addOption(OptionType.BOOLEAN, "inversion", "Show latest logs first (default set to true). " +
                                "Options: True / False", false)
                        .addOption(OptionType.STRING, "datetime", "Date and time from which the logs start. " +
                                "Option example: \"2023-04-18 00:00:00\"", false)
                        .addOption(OptionType.INTEGER, "rowcount", "The number of log lines (default is 1,000). " +
                                "Max: 32000 Min: 50", false)
                        .queue();
                guild.upsertCommand("polishcat", "Funny song").queue();
            }
        } catch(InterruptedException ignored) {}
    }
}