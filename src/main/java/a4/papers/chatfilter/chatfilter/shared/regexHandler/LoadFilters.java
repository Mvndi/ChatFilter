package a4.papers.chatfilter.chatfilter.shared.regexHandler;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import a4.papers.chatfilter.chatfilter.shared.Normalizer;
import a4.papers.chatfilter.chatfilter.shared.UnicodeWrapper;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LoadFilters {

    private final Normalizer normalizer = new Normalizer();
    ChatFilter chatFilter;

    public LoadFilters(ChatFilter instance) {
        chatFilter = instance;
    }

    public void loadWordFilter() {
        chatFilter.swearWordRegistry.clear();
        ConfigurationSection root = chatFilter.getWordConfig().getConfigurationSection("ChatFilter");
        if (root == null) {
            return;
        }

        for (String key : root.getKeys(false)) {
            ConfigurationSection word = root.getConfigurationSection(key);
            if (word == null) {
                continue;
            }
            boolean informConsole = word.getBoolean("Warn.Console");
            boolean msgToStaff = word.getBoolean("Warn.Staff");
            boolean msgToPlayer = word.getBoolean("Warn.Player");
            boolean cancelChat = word.getBoolean("CancelChat.Cancel");
            boolean cancelChatReplace = word.getBoolean("CancelChat.Replace");
            String replaceWith = word.getString("CancelChat.ReplaceWith");
            List<String> command = word.getStringList("Action");
            boolean enabled = word.getBoolean("Enabled");
            List<String> canonicalWords = resolveCanonicalWords(word);
            if (word.getStringList("Regex").isEmpty()) {
                String regex = word.getString("Regex");
                if (enabled) {
                    FilterWrapper filterWrapper = new FilterWrapper(key, command, regex, cancelChat, cancelChatReplace, replaceWith, msgToStaff, informConsole, msgToPlayer);
                    chatFilter.regexWords.put(regex, filterWrapper);
                    registerCanonicalWords(canonicalWords, filterWrapper);
                }
            } else {
                List<String> regexList = word.getStringList("Regex");
                for (String regex : regexList) {
                    if (enabled) {
                        FilterWrapper filterWrapper = new FilterWrapper(key, command, regex, cancelChat, cancelChatReplace, replaceWith, msgToStaff, informConsole, msgToPlayer);
                        chatFilter.regexWords.put(regex, filterWrapper);
                        registerCanonicalWords(canonicalWords, filterWrapper);
                    }
                }
            }
        }
    }

    public void loadAdvertFilter() {
        ConfigurationSection root = chatFilter.getAdvertConfig().getConfigurationSection("ChatFilter");
        if (root == null) {
            return;
        }

        for (String key : root.getKeys(false)) {
            ConfigurationSection word = root.getConfigurationSection(key);
            if (word == null) {
                continue;
            }
            boolean informConsole = word.getBoolean("Warn.Console");
            boolean msgToStaff = word.getBoolean("Warn.Staff");
            boolean msgToPlayer = word.getBoolean("Warn.Player");
            boolean cancelChat = word.getBoolean("CancelChat.Cancel");
            boolean cancelChatReplace = word.getBoolean("CancelChat.Replace");
            String replaceWith = word.getString("CancelChat.ReplaceWith");
            List<String> command = word.getStringList("Action");
            boolean enabled = word.getBoolean("Enabled");

            if (word.getStringList("Regex").isEmpty()) {
                String regex = word.getString("Regex");
                if (enabled)
                    chatFilter.regexAdvert.put(regex, new FilterWrapper(key, command, regex, cancelChat, cancelChatReplace, replaceWith, msgToStaff, informConsole, msgToPlayer));
            } else {
                List<String> regexList = word.getStringList("Regex");
                for (String regex : regexList) {
                    if (enabled)
                        chatFilter.regexAdvert.put(regex, new FilterWrapper(key, command, regex, cancelChat, cancelChatReplace, replaceWith, msgToStaff, informConsole, msgToPlayer));
                }
            }
        }
    }

    public void loadUnicodeFilter() {
        chatFilter.unicodeWhitelist.clear();
        chatFilter.unicodeBlacklist.clear();
        loadUnicodeWhitelist();
        ConfigurationSection root = chatFilter.getUnicodeConfig().getConfigurationSection("Unicode.blacklist");
        if (root == null) {
            return;
        }

        for (String key : root.getKeys(false)) {
            ConfigurationSection word = root.getConfigurationSection(key);
            if (word == null) {
                continue;
            }
            String start = word.getString("range-start");
            String end = word.getString("range-end");
            chatFilter.unicodeBlacklist.put(key, new UnicodeWrapper(start, end));
        }
    }
    public void loadUnicodeWhitelist() {
        if (chatFilter.getUnicodeConfig().getStringList("Unicode.whitelist").isEmpty()) {
            String s = chatFilter.getUnicodeConfig().getString("Unicode.whitelist");
            chatFilter.unicodeWhitelist.add(s);
        } else {
            List<String> List = chatFilter.getUnicodeConfig().getStringList("Unicode.whitelist");
            chatFilter.unicodeWhitelist.addAll(List);
        }
    }

    public void createWordFilter(String word, String sender) {
        createWordFilter(word, sender, true);
    }

    public void createWordFilter(String word, String sender, boolean importAsWord) {
        String regex = chatFilter.regexpGenerator().generateRegexp(word);
        if (chatFilter.perWordOptionsEnable) {
            chatFilter.getWordConfig().set("ChatFilter." + word + ".Enabled", chatFilter.defaultWordEnabled);
            if (importAsWord) {
                chatFilter.getWordConfig().set("ChatFilter." + word + ".Word", word);
            }
            chatFilter.getWordConfig().set("ChatFilter." + word + ".Regex", regex);
            chatFilter.getWordConfig().set("ChatFilter." + word + ".Warn.Staff", chatFilter.defaultWordWarnStaff);
            chatFilter.getWordConfig().set("ChatFilter." + word + ".Warn.Player", chatFilter.defaultWordWarnPlayer);
            chatFilter.getWordConfig().set("ChatFilter." + word + ".Warn.Console", chatFilter.defaultWordWarnConsole);
            chatFilter.getWordConfig().set("ChatFilter." + word + ".CancelChat.Cancel", chatFilter.defaultWordCancelChatCancel);
            chatFilter.getWordConfig().set("ChatFilter." + word + ".CancelChat.Replace", chatFilter.defaultWordCancelReplace);
            chatFilter.getWordConfig().set("ChatFilter." + word + ".CancelChat.ReplaceWith", chatFilter.defaultWordCancelReplaceWith);
            chatFilter.getWordConfig().set("ChatFilter." + word + ".Action", chatFilter.defaultWordAction);
            chatFilter.getWordConfig().set("ChatFilter." + word + ".AddedBy", sender);
        } else {
            ConfigurationSection key = chatFilter.getWordConfig().getConfigurationSection("ChatFilter." + chatFilter.perWordOptionsString);
            if (key == null) {
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString, "");
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".Enabled", chatFilter.defaultWordEnabled);
                if (importAsWord) {
                    chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".Word", word);
                }
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".Regex", regex);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".Warn.Staff", chatFilter.defaultWordWarnStaff);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".Warn.Player", chatFilter.defaultWordWarnPlayer);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".Warn.Console", chatFilter.defaultWordWarnConsole);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".CancelChat.Cancel", chatFilter.defaultWordCancelChatCancel);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".CancelChat.Replace", chatFilter.defaultWordCancelReplace);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".CancelChat.ReplaceWith", chatFilter.defaultWordCancelReplaceWith);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".Action", chatFilter.defaultWordAction);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".AddedBy", sender);
            } else {
                List<String> regexList = key.getStringList("Regex");
                regexList.add(regex);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".Regex", regexList);
            }
        }
        chatFilter.save();
        reloadFilters();
        Pattern p = Pattern.compile(regex);
        chatFilter.wordRegexPattern.add(p);
    }

    public void createAdvertFilter(String s, String sender) {
        String notDot = s.replace(".", "");
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Enabled", chatFilter.defaultIPEnabled);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Regex", s);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Warn.Staff", chatFilter.defaultIPWarnStaff);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Warn.Player", chatFilter.defaultIPWarnPlayer);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Warn.Console", chatFilter.defaultIPWarnConsole);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".CancelChat.Cancel", chatFilter.defaultIPCancelChatCancel);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".CancelChat.Replace", chatFilter.defaultIPCancelReplace);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".CancelChat.ReplaceWith", chatFilter.defaultIPCancelReplaceWith);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Action", chatFilter.defaultIPAction);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".AddedBy", sender);
        chatFilter.save();
        reloadFilters();
        Pattern p = Pattern.compile(s);
        chatFilter.advertRegexPattern.add(p);
    }

    public void reloadFilters() {
        chatFilter.regexAdvert.clear();
        chatFilter.regexWords.clear();
        chatFilter.advertRegexPattern.clear();
        chatFilter.wordRegexPattern.clear();
        loadAdvertFilter();
        loadWordFilter();
        regexCompile();
    }

    public void regexCompile() {
        for (String StringMatchedDNS : chatFilter.regexAdvert.keySet()) {
            Pattern p = Pattern.compile(StringMatchedDNS);
            chatFilter.advertRegexPattern.add(p);
        }
        for (String StringMatchedWords : chatFilter.regexWords.keySet()) {
            Pattern p = Pattern.compile(StringMatchedWords);
            chatFilter.wordRegexPattern.add(p);
        }
    }

    private void registerCanonicalWords(List<String> canonicalWords, FilterWrapper filterWrapper) {
        for (String canonicalWord : canonicalWords) {
            if (!shouldRegisterCanonicalWord(canonicalWord)) {
                continue;
            }
            chatFilter.swearWordRegistry.register(canonicalWord, filterWrapper);
        }
    }

    private List<String> resolveCanonicalWords(ConfigurationSection word) {
        List<String> words = new ArrayList<>();

        List<String> configuredWords = word.getStringList("Words");
        if (!configuredWords.isEmpty()) {
            words.addAll(configuredWords);
            return words;
        }

        String configuredWord = word.getString("Word");
        if (configuredWord != null && !configuredWord.trim().isEmpty()) {
            words.add(configuredWord.trim());
            return words;
        }

        return words;
    }

    private boolean shouldRegisterCanonicalWord(String canonicalWord) {
        if (canonicalWord == null || canonicalWord.trim().isEmpty()) {
            return false;
        }

        String normalizedWord = normalizer.normalize(canonicalWord);
        if (normalizedWord.isEmpty()) {
            return false;
        }

        String comparableSource = stripNonAlphanumeric(normalizedWord);
        if (comparableSource.isEmpty()) {
            return false;
        }

        if (comparableSource.length() > 4) {
            return true;
        }

        return comparableSource.equals(normalizer.aggressiveNormalize(comparableSource));
    }

    private String stripNonAlphanumeric(String input) {
        StringBuilder builder = new StringBuilder(input.length());
        for (int index = 0; index < input.length(); index++) {
            char current = input.charAt(index);
            if (Character.isLetterOrDigit(current)) {
                builder.append(current);
            }
        }
        return builder.toString();
    }
}
