package main.api.app;

public class exceptions {
    public static class TelegramError extends Exception {
        public TelegramError(String message) {
            super(message);
        }
    }

    public static class StartupError extends TelegramError {
        public StartupError(String message) {
            super(message);
        }
    }
}
