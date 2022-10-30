public class TestPath {
    public static void main(String[] args) {
        String uriPattern = "/alarm/{alarmId}/{moType}/get";
        StringBuilder uriRePattern = new StringBuilder();
        int begin,end = 0;
        while ((begin = uriPattern.indexOf('{', end)) != -1) {

            uriRePattern.append(uriPattern.substring(end, begin));
            end = uriPattern.indexOf('}', begin) + 1;
            if (end == 0) {
                throw new RuntimeException("end == 0");
            }
            String name = uriPattern.substring(begin + 1, end - 1);
            uriRePattern.append("(?<" + name + ">[^/]+)");
        }
        uriRePattern.append(uriPattern.substring(end));
        System.out.println(uriRePattern.toString());
    }
}
