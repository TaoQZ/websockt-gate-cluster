//package xyz.taoqz.config;
//
//import org.springframework.util.StringUtils;
//
//import javax.servlet.ServletRequest;
//import javax.servlet.http.HttpServletRequest;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class RequestUtils {
//    /**
//     * 获取访问者IP
//     * <p>
//     * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
//     * <p>
//     * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，
//     * 如果还不存在则调用Request .getRemoteAddr()。
//     *
//     * @param request
//     * @return
//     */
//    public static String getIpAddr(HttpServletRequest request) {
//        String ip = request.getHeader("X-Real-IP");
//        if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
//            return getIP(ip);
//        }
//        ip = request.getHeader("X-Forwarded-For");
//        if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
//            // 多次反向代理后会有多个IP值，第一个为真实IP。
//            int index = ip.indexOf(',');
//            if (index != -1) {
//                return getIP(ip.substring(0, index));
//            } else {
//                return getIP(ip);
//            }
//        } else {
//            return getIP(request.getRemoteAddr());
//        }
//    }
//
//
//    /**
//     * 从正则表达式里面提取字符
//     *
//     * @param s
//     * @return
//     */
//    private static String getIP(String s) {
//        String rexp = "([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})";
//        Pattern p = Pattern.compile(rexp);
//        Matcher m = p.matcher(s);
//        boolean result = m.find();
//        String ip = "0.0.0.0";
//        while (result) {
//            ip = m.group(1);
//            result = m.find();
//        }
//        return ip;
//
//    }
//
//    /**
//     * 解析请求contentType
//     */
//    public static String getContentType(ServletRequest request) {
//        String contentType = request.getContentType();
//        if (contentType == null) {
//            contentType = "";
//        }
//        int semicolon = contentType.indexOf(';');
//        if (semicolon >= 0) {
//            contentType = contentType.substring(0, semicolon).trim();
//        } else {
//            contentType = contentType.trim();
//        }
//        return contentType;
//    }
//
////    public static String getNetIp(HttpServletRequest request) {
////        Set<String> set = new HashSet<>();
////        String ip = request.getHeader("X-Real-IP");
////        if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
////            String tIp = getIP(ip);
////            if(!StringUtils.isEmpty(tIp)){
////                set.add(tIp);
////            }
////        }
////        ip = request.getHeader("X-Forwarded-For");
////        if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
////            String[] ipArr =  ip.split(",");
////            if(ipArr != null && ipArr.length > 0){
////                for(String strIp : ipArr){
////                    String tIp = getIP(strIp);
////                    if(!StringUtils.isEmpty(tIp)){
////                        set.add(tIp);
////                    }
////                }
////            }else{
////                String tIp = getIP(ip);
////                if(!StringUtils.isEmpty(tIp)){
////                    set.add(tIp);
////                }
////            }
////        }
////        ip = request.getRemoteAddr();
////        if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
////            String tIp = getIP(ip);
////            if(!StringUtils.isEmpty(tIp)){
////                set.add(tIp);
////            }
////        }
////        //移除本机ip
////        set.remove("127.0.0.1");
////        StringJoiner sj = new StringJoiner(",");
////        for(String tIp : set){
////            sj.add(tIp);
////        }
////        return sj.toString();
////    }
//}
