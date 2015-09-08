import java.io.PrintStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CSSCodec implements Codec {
	private static final Log log = LogFactory.getLog(CSSCodec.class);

	public String encode(String input) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < input.length(); ++i) {
			char c = input.charAt(i);
			sb.append(encodeCharacter(new Character(c)));
		}
		return sb.toString();
	}

	public String encodeCharacter(Character c) {
		char ch = c.charValue();

		if (ch <= 256) {
			return "\\" + ch;
		}

		String temp = Integer.toHexString(ch);
		return "\\" + temp.toUpperCase() + " ";
	}

	public String decode(String input) {
		StringBuffer sb = new StringBuffer();
		PushbackString pbs = new PushbackString(input);
		while (pbs.hasNext()) {
			Character c = decodeCharacter(pbs);
			if (c != null)
				sb.append(c);
			else {
				sb.append(pbs.next());
			}
		}
		return sb.toString();
	}

	public Character decodeCharacter(PushbackString input) {
		input.mark();
		Character first = input.next();
		if (first == null) {
			input.reset();
			return null;
		}

		if (first.charValue() != '\\') {
			input.reset();
			return null;
		}

		Character second = input.next();
		if (second == null) {
			input.reset();
			return null;
		}

		System.out.println(">>>" + second);

		if (input.isHexDigit(second)) {
			StringBuffer sb = new StringBuffer();
			sb.append(second);
			for (int i = 0; i < 5; ++i) {
				Character c = input.next();
				if (c == null)
					break;
				if (c.charValue() == ' ')
					break;
				if (input.isHexDigit(c)) {
					sb.append(c);
				} else {
					input.pushback(c);
					break;
				}
			}
			try {
				int ret = Integer.parseInt(sb.toString(), 16);

				return new Character((char) ret);
			} catch (NumberFormatException e) {
				log.warn("NumberFormatException", e);
			}
		}

		return second;
	}
}
