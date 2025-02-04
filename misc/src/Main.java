//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        boolean[] engagement = { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false };
        System.out.println(question("str", engagement, 2));
    }
    
    public static int question(String video, boolean[] engagement, int k) {
        int result = 0;
        int countNonViral = engagement[video.charAt(0) - 'a'] ? 0 : 1;

        // l represents the character at the start of a valid string;
        // r represents the character after the end of a valid string
        int l = 0;
        int r = 1;
        
        while (l < video.length() && r < video.length()) {
            while (r < video.length() && countNonViral <= k) {
                result += r - l;
                if (!engagement[video.charAt(r) - 'a']) {
                    countNonViral++;
                }
                r++;
            }
            while (l < video.length() && countNonViral > k && engagement[video.charAt(l) - 'a']) {
                l++;
            }
            l++;
            countNonViral--;
        }
        if (countNonViral <= k) {
            result += r - l;
        }
        return result;
    }
}