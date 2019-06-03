package net.fender.pogo;

import java.util.HashMap;
import java.util.Map;

public class CpModifier {

    private static final Map<Double, Double> MODIFIERS = new HashMap<>();

    static {
        MODIFIERS.put(1.0, 0.094);
        MODIFIERS.put(1.5, 0.135137432);
        MODIFIERS.put(2.0, 0.16639787);
        MODIFIERS.put(2.5, 0.192650919);
        MODIFIERS.put(3.0, 0.21573247);
        MODIFIERS.put(3.5, 0.236572661);
        MODIFIERS.put(4.0, 0.25572005);
        MODIFIERS.put(4.5, 0.273530381);
        MODIFIERS.put(5.0, 0.29024988);
        MODIFIERS.put(5.5, 0.306057377);
        MODIFIERS.put(6.0, 0.3210876);
        MODIFIERS.put(6.5, 0.335445036);
        MODIFIERS.put(7.0, 0.34921268);
        MODIFIERS.put(7.5, 0.362457751);
        MODIFIERS.put(8.0, 0.37523559);
        MODIFIERS.put(8.5, 0.387592406);
        MODIFIERS.put(9.0, 0.39956728);
        MODIFIERS.put(9.5, 0.411193551);
        MODIFIERS.put(10.0, 0.42250001);
        MODIFIERS.put(10.5, 0.432926419);
        MODIFIERS.put(11.0, 0.44310755);
        MODIFIERS.put(11.5, 0.4530599578);
        MODIFIERS.put(12.0, 0.46279839);
        MODIFIERS.put(12.5, 0.472336083);
        MODIFIERS.put(13.0, 0.48168495);
        MODIFIERS.put(13.5, 0.4908558);
        MODIFIERS.put(14.0, 0.49985844);
        MODIFIERS.put(14.5, 0.508701765);
        MODIFIERS.put(15.0, 0.51739395);
        MODIFIERS.put(15.5, 0.525942511);
        MODIFIERS.put(16.0, 0.53435433);
        MODIFIERS.put(16.5, 0.542635767);
        MODIFIERS.put(17.0, 0.55079269);
        MODIFIERS.put(17.5, 0.558830576);
        MODIFIERS.put(18.0, 0.56675452);
        MODIFIERS.put(18.5, 0.574569153);
        MODIFIERS.put(19.0, 0.58227891);
        MODIFIERS.put(19.5, 0.589887917);
        MODIFIERS.put(20.0, 0.59740001);
        MODIFIERS.put(20.5, 0.604818814);
        MODIFIERS.put(21.0, 0.61215729);
        MODIFIERS.put(21.5, 0.619399365);
        MODIFIERS.put(22.0, 0.62656713);
        MODIFIERS.put(22.5, 0.633644533);
        MODIFIERS.put(23.0, 0.64065295);
        MODIFIERS.put(23.5, 0.647576426);
        MODIFIERS.put(24.0, 0.65443563);
        MODIFIERS.put(24.5, 0.661214806);
        MODIFIERS.put(25.0, 0.667934);
        MODIFIERS.put(25.5, 0.674577537);
        MODIFIERS.put(26.0, 0.68116492);
        MODIFIERS.put(26.5, 0.687680648);
        MODIFIERS.put(27.0, 0.69414365);
        MODIFIERS.put(27.5, 0.700538673);
        MODIFIERS.put(28.0, 0.70688421);
        MODIFIERS.put(28.5, 0.713164996);
        MODIFIERS.put(29.0, 0.71939909);
        MODIFIERS.put(29.5, 0.725571552);
        MODIFIERS.put(30.0, 0.7317);
        MODIFIERS.put(30.5, 0.734741009);
        MODIFIERS.put(31.0, 0.73776948);
        MODIFIERS.put(31.5, 0.740785574);
        MODIFIERS.put(32.0, 0.74378943);
        MODIFIERS.put(32.5, 0.746781211);
        MODIFIERS.put(33.0, 0.74976104);
        MODIFIERS.put(33.5, 0.752729087);
        MODIFIERS.put(34.0, 0.75568551);
        MODIFIERS.put(34.5, 0.758630378);
        MODIFIERS.put(35.0, 0.76156384);
        MODIFIERS.put(35.5, 0.764486065);
        MODIFIERS.put(36.0, 0.76739717);
        MODIFIERS.put(36.5, 0.770297266);
        MODIFIERS.put(37.0, 0.7731865);
        MODIFIERS.put(37.5, 0.776064962);
        MODIFIERS.put(38.0, 0.77893275);
        MODIFIERS.put(38.5, 0.781790055);
        MODIFIERS.put(39.0, 0.78463697);
        MODIFIERS.put(39.5, 0.787473578);
        MODIFIERS.put(40.0, 0.79030001);
    }

    public static double getCpModifier(double level) {
        return MODIFIERS.get(level);
    }
}
