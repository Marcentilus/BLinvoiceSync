
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BLIdParser implements IdParser{

    public String parseId(String valueToParse) {

        String pattern = "BLID:(\\d+);";


        Pattern regex = Pattern.compile(pattern);

        Matcher matcher = regex.matcher(valueToParse);

        if (matcher.find()) {

            return matcher.group(1);
        } else {

            return "";
        }
    }
}