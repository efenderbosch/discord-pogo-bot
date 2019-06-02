package net.fender.pogo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CpModifier {

    private static final Map<Double, BigDecimal> MODIFIERS = new HashMap<>();

    static {
        MODIFIERS.put(1.0, new BigDecimal("0.094"));
        MODIFIERS.put(1.5, new BigDecimal("0.135137432"));
        MODIFIERS.put(2.0, new BigDecimal("0.16639787"));
        MODIFIERS.put(2.5, new BigDecimal("0.192650919"));
        MODIFIERS.put(3.0, new BigDecimal("0.21573247"));
        MODIFIERS.put(3.5, new BigDecimal("0.236572661"));
        MODIFIERS.put(4.0, new BigDecimal("0.25572005"));
        MODIFIERS.put(4.5, new BigDecimal("0.273530381"));
        MODIFIERS.put(5.0, new BigDecimal("0.29024988"));
        MODIFIERS.put(5.5, new BigDecimal("0.306057377"));
        MODIFIERS.put(6.0, new BigDecimal("0.3210876"));
        MODIFIERS.put(6.5, new BigDecimal("0.335445036"));
        MODIFIERS.put(7.0, new BigDecimal("0.34921268"));
        MODIFIERS.put(7.5, new BigDecimal("0.362457751"));
        MODIFIERS.put(8.0, new BigDecimal("0.37523559"));
        MODIFIERS.put(8.5, new BigDecimal("0.387592406"));
        MODIFIERS.put(9.0, new BigDecimal("0.39956728"));
        MODIFIERS.put(9.5, new BigDecimal("0.411193551"));
        MODIFIERS.put(10.0, new BigDecimal("0.42250001"));
        MODIFIERS.put(10.5, new BigDecimal("0.432926419"));
        MODIFIERS.put(11.0, new BigDecimal("0.44310755"));
        MODIFIERS.put(11.5, new BigDecimal("0.4530599578"));
        MODIFIERS.put(12.0, new BigDecimal("0.46279839"));
        MODIFIERS.put(12.5, new BigDecimal("0.472336083"));
        MODIFIERS.put(13.0, new BigDecimal("0.48168495"));
        MODIFIERS.put(13.5, new BigDecimal("0.4908558"));
        MODIFIERS.put(14.0, new BigDecimal("0.49985844"));
        MODIFIERS.put(14.5, new BigDecimal("0.508701765"));
        MODIFIERS.put(15.0, new BigDecimal("0.51739395"));
        MODIFIERS.put(15.5, new BigDecimal("0.525942511"));
        MODIFIERS.put(16.0, new BigDecimal("0.53435433"));
        MODIFIERS.put(16.5, new BigDecimal("0.542635767"));
        MODIFIERS.put(17.0, new BigDecimal("0.55079269"));
        MODIFIERS.put(17.5, new BigDecimal("0.558830576"));
        MODIFIERS.put(18.0, new BigDecimal("0.56675452"));
        MODIFIERS.put(18.5, new BigDecimal("0.574569153"));
        MODIFIERS.put(19.0, new BigDecimal("0.58227891"));
        MODIFIERS.put(19.5, new BigDecimal("0.589887917"));
        MODIFIERS.put(20.0, new BigDecimal("0.59740001"));
        MODIFIERS.put(20.5, new BigDecimal("0.604818814"));
        MODIFIERS.put(21.0, new BigDecimal("0.61215729"));
        MODIFIERS.put(21.5, new BigDecimal("0.619399365"));
        MODIFIERS.put(22.0, new BigDecimal("0.62656713"));
        MODIFIERS.put(22.5, new BigDecimal("0.633644533"));
        MODIFIERS.put(23.0, new BigDecimal("0.64065295"));
        MODIFIERS.put(23.5, new BigDecimal("0.647576426"));
        MODIFIERS.put(24.0, new BigDecimal("0.65443563"));
        MODIFIERS.put(24.5, new BigDecimal("0.661214806"));
        MODIFIERS.put(25.0, new BigDecimal("0.667934"));
        MODIFIERS.put(25.5, new BigDecimal("0.674577537"));
        MODIFIERS.put(26.0, new BigDecimal("0.68116492"));
        MODIFIERS.put(26.5, new BigDecimal("0.687680648"));
        MODIFIERS.put(27.0, new BigDecimal("0.69414365"));
        MODIFIERS.put(27.5, new BigDecimal("0.700538673"));
        MODIFIERS.put(28.0, new BigDecimal("0.70688421"));
        MODIFIERS.put(28.5, new BigDecimal("0.713164996"));
        MODIFIERS.put(29.0, new BigDecimal("0.71939909"));
        MODIFIERS.put(29.5, new BigDecimal("0.725571552"));
        MODIFIERS.put(30.0, new BigDecimal("0.7317"));
        MODIFIERS.put(30.5, new BigDecimal("0.734741009"));
        MODIFIERS.put(31.0, new BigDecimal("0.73776948"));
        MODIFIERS.put(31.5, new BigDecimal("0.740785574"));
        MODIFIERS.put(32.0, new BigDecimal("0.74378943"));
        MODIFIERS.put(32.5, new BigDecimal("0.746781211"));
        MODIFIERS.put(33.0, new BigDecimal("0.74976104"));
        MODIFIERS.put(33.5, new BigDecimal("0.752729087"));
        MODIFIERS.put(34.0, new BigDecimal("0.75568551"));
        MODIFIERS.put(34.5, new BigDecimal("0.758630378"));
        MODIFIERS.put(35.0, new BigDecimal("0.76156384"));
        MODIFIERS.put(35.5, new BigDecimal("0.764486065"));
        MODIFIERS.put(36.0, new BigDecimal("0.76739717"));
        MODIFIERS.put(36.5, new BigDecimal("0.770297266"));
        MODIFIERS.put(37.0, new BigDecimal("0.7731865"));
        MODIFIERS.put(37.5, new BigDecimal("0.776064962"));
        MODIFIERS.put(38.0, new BigDecimal("0.77893275"));
        MODIFIERS.put(38.5, new BigDecimal("0.781790055"));
        MODIFIERS.put(39.0, new BigDecimal("0.78463697"));
        MODIFIERS.put(39.5, new BigDecimal("0.787473578"));
        MODIFIERS.put(40.0, new BigDecimal("0.79030001"));
    }

    public static BigDecimal getCpModifier(double level) {
        return MODIFIERS.get(level);
    }
}
