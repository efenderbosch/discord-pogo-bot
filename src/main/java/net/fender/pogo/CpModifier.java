package net.fender.pogo;

import java.util.HashMap;
import java.util.Map;

public class CpModifier {

    private static final Map<Double, Double> MODIFIERS = new HashMap<>();

    static {
        MODIFIERS.put(1.0, 0.094);
        MODIFIERS.put(1.5, 0.1351374318);
        MODIFIERS.put(2.0, 0.16639787);
        MODIFIERS.put(2.5, 0.192650919);
        MODIFIERS.put(3.0, 0.21573247);
        MODIFIERS.put(3.5, 0.2365726613);
        MODIFIERS.put(4.0, 0.25572005);
        MODIFIERS.put(4.5, 0.2735303812);
        MODIFIERS.put(5.0, 0.29024988);
        MODIFIERS.put(5.5, 0.3060573775);
        MODIFIERS.put(6.0, 0.3210876);
        MODIFIERS.put(6.5, 0.3354450362);
        MODIFIERS.put(7.0, 0.34921268);
        MODIFIERS.put(7.5, 0.3624577511);
        // 0.37523559?
        MODIFIERS.put(8.0, 0.37523559);
        MODIFIERS.put(8.5, 0.387592416);
        MODIFIERS.put(9.0, 0.39956728);
        MODIFIERS.put(9.5, 0.4111935514);
        MODIFIERS.put(10.0, 0.4225);
        MODIFIERS.put(10.5, 0.4329264091);
        MODIFIERS.put(11.0, 0.44310755);
        MODIFIERS.put(11.5, 0.4530599591);
        MODIFIERS.put(12.0, 0.4627984);
        MODIFIERS.put(12.5, 0.472336093);
        MODIFIERS.put(13.0, 0.48168495);
        MODIFIERS.put(13.5, 0.4908558003);
        MODIFIERS.put(14.0, 0.49985844);
        MODIFIERS.put(14.5, 0.508701765);
        MODIFIERS.put(15.0, 0.51739395);
        MODIFIERS.put(15.5, 0.5259425113);
        MODIFIERS.put(16.0, 0.5343543);
        MODIFIERS.put(16.5, 0.5426357375);
        MODIFIERS.put(17.0, 0.5507927);
        MODIFIERS.put(17.5, 0.5588305862);
        MODIFIERS.put(18.0, 0.5667545);
        MODIFIERS.put(18.5, 0.5745691333);
        MODIFIERS.put(19.0, 0.5822789);
        MODIFIERS.put(19.5, 0.5898879072);
        MODIFIERS.put(20.0, 0.5974);
        MODIFIERS.put(20.5, 0.6048236651);
        MODIFIERS.put(21.0, 0.6121573);
        MODIFIERS.put(21.5, 0.6194041216);
        MODIFIERS.put(22.0, 0.6265671);
        MODIFIERS.put(22.5, 0.6336491432);
        MODIFIERS.put(23.0, 0.64065295);
        MODIFIERS.put(23.5, 0.6475809666);
        MODIFIERS.put(24.0, 0.65443563);
        MODIFIERS.put(24.5, 0.6612192524);
        MODIFIERS.put(25.0, 0.667934);
        MODIFIERS.put(25.5, 0.6745818959);
        MODIFIERS.put(26.0, 0.6811649);
        MODIFIERS.put(26.5, 0.6876849038);
        MODIFIERS.put(27.0, 0.69414365);
        MODIFIERS.put(27.5, 0.70054287);
        MODIFIERS.put(28.0, 0.7068842);
        MODIFIERS.put(28.5, 0.7131691091);
        MODIFIERS.put(29.0, 0.7193991);
        MODIFIERS.put(29.5, 0.7255756136);
        MODIFIERS.put(30.0, 0.7317);
        MODIFIERS.put(30.5, 0.7347410093);
        MODIFIERS.put(31.0, 0.7377695);
        MODIFIERS.put(31.5, 0.7407855938);
        MODIFIERS.put(32.0, 0.74378943);
        MODIFIERS.put(32.5, 0.7467812109);
        MODIFIERS.put(33.0, 0.74976104);
        MODIFIERS.put(33.5, 0.7527290867);
        MODIFIERS.put(34.0, 0.7556855);
        MODIFIERS.put(34.5, 0.7586303683);
        MODIFIERS.put(35.0, 0.76156384);
        MODIFIERS.put(35.5, 0.7644860647);
        MODIFIERS.put(36.0, 0.76739717);
        MODIFIERS.put(36.5, 0.7702972656);
        MODIFIERS.put(37.0, 0.7731865);
        MODIFIERS.put(37.5, 0.7760649616);
        MODIFIERS.put(38.0, 0.77893275);
        MODIFIERS.put(38.5, 0.7817900548);
        MODIFIERS.put(39.0, 0.784637);
        MODIFIERS.put(39.5, 0.7874736075);
        MODIFIERS.put(40.0, 0.7903);
    }

    public static double getCpModifier(double level) {
        return MODIFIERS.get(level);
    }
}
