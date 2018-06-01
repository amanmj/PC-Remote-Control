package PC_Remote_Controller;
/**
 *
 * @author Aman Mahajan
 */
public class Main {
    public static void main(String[] args) {
    String url="https://api.telegram.org/bot";
    String tokenID="enter_token_id";
    new amanmjBot(url,tokenID).start();
    }
}
