package cc.catalysts.asdoclet.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import cc.catalysts.asdoclet.generator.Generator;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;

public class TagParser {
	private static final Pattern parseCommandPattern = Pattern.compile("(\\S+)\\s*=\\s*(\\S+)");

	public static boolean getBooleanCommand(String command, Map<String, String> commands) {
		boolean b = false;
		if (commands.containsKey(command)) b = Boolean.parseBoolean(commands.get(command));
		return b;
	}

	public static String getStringCommand(String command, Map<String, String> commands) {
		return getStringCommand(command, null, commands);
	}

	public static String getStringCommand(String command, String defaultValue, Map<String, String> commands) {
		String s = defaultValue;
		if (commands.containsKey(command)) s = String.valueOf(commands.get(command));
		return s;
	}

	public static boolean hasClassTags(Generator generator, ClassDoc classDoc) {
		Tag[] tags = classDoc.tags(generator.getName() + Constants.TAG_CLASS);
		return tags.length > 0;
	}

	public static Map<String, String> processClassTags(Generator generator, ClassDoc classDoc) {
		Map<String, String> commands = new HashMap<String, String>();
		processTags(classDoc.tags(generator.getName() + Constants.TAG_CLASS), commands);
		processTags(classDoc.tags(generator.getName() + Constants.TAG_METHOD), commands);
		processTags(classDoc.tags(generator.getName() + Constants.TAG_PROPERTY), commands);
		return commands;
	}

	public static void processTags(Tag[] tags, Map<String, String> commands) {
		for (Tag tag : tags) processTag(tag, commands);
	}

	private static void processTag(Tag tag, Map<String, String> commands) {
		Matcher matcher = parseCommandPattern.matcher(tag.text());
		while (matcher.find()) {
			String value = matcher.group(2).replaceAll("<.*?>", "");
			value = value.replaceAll("&lt;", "<");
			value = value.replaceAll("&gt;", ">");
			commands.put(matcher.group(1), value);
		}
	}
}
