package me.andrew.discordsupport;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No bot token provided");
        }else{
            new DiscordSupportBot(args[0],null);
        }


    }

}
