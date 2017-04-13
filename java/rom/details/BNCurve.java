package rom.details;

import rom.field.FieldDetails;

public class BNCurve implements CurveDetails {

	// BN Curve
	
	public static final int CURVETYPE = FieldDetails.WEIERSTRASS;
	public static final int CURVE_A = 0;
	
	public static final int[] CURVE_B = {0x2,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0};
	public static final int[] CURVE_Order={0xD,0x8000000,0x428,0x1F000000,0x7FF9,0x6C00000,0x6E8D1,0x10480000,0x252364};
	public static final int[] CURVE_Bnx={0x1,0x4000000,0x10,0x0,0x0,0x0,0x0,0x0,0x0};
	public static final int[] CURVE_Cru={0x7,0xC000000,0x1B3,0x12000000,0x2490,0x11200000,0x126CD,0x0,0x0};
	public static final int[] CURVE_Fra={0xF2A6DE9,0xBEF3603,0xFDDF0B8,0x12E9249A,0x953F850,0xDA85423,0x1232D926,0x32425CF,0x1B3776};
	public static final int[] CURVE_Frb={0x10D5922A,0xC10C9FC,0x10221431,0xF16DB65,0x16AC8DC1,0x1917ABDC,0xDD40FAA,0xD23DA30,0x9EBEE};
	public static final int[] CURVE_Pxa={0x15FD0CB4,0x1D5963C9,0x1F315F0A,0xBC633C9,0x1763B05A,0x1B927B6F,0x1FA8CD7E,0x1A9EABD4,0x95B04};
	public static final int[] CURVE_Pxb={0x10962455,0x503E83C,0x9EA978E,0x1B0D7C7A,0x147F39D6,0x1FC4F02B,0x1ED2750A,0x14F81068,0x5D4D8};
	public static final int[] CURVE_Pya={0x1A08A46C,0xD6E7343,0x290647E,0x105661D3,0xB1F1690,0xE261BC2,0x4FE85B4,0x17E4BCA6,0xABF2A};
	public static final int[] CURVE_Pyb={0x5F306EC,0x16FC46A0,0x1744E839,0x9040ED5,0x19D6A5C0,0x138F23C0,0xAF6CE18,0x10FCCF3B,0x18769A};
	public static final int[] CURVE_Gx ={0x12,0x18000000,0x4E9,0x2000000,0x8612,0x6C00000,0x6E8D1,0x10480000,0x252364};
	public static final int[] CURVE_Gy ={0x1,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0};
	
	public static final int[][] CURVE_W={{0x3,0x0,0x81,0x3000000,0x618,0x0,0x0,0x0,0x0},{0x1,0x8000000,0x20,0x0,0x0,0x0,0x0,0x0,0x0}};
	public static final int[][][] CURVE_SB={{{0x4,0x8000000,0xA1,0x3000000,0x618,0x0,0x0,0x0,0x0},{0x1,0x8000000,0x20,0x0,0x0,0x0,0x0,0x0,0x0}},{{0x1,0x8000000,0x20,0x0,0x0,0x0,0x0,0x0,0x0},{0xA,0x8000000,0x3A7,0x1C000000,0x79E1,0x6C00000,0x6E8D1,0x10480000,0x252364}}};
	public static final int[][] CURVE_WB={{0x0,0x4000000,0x10,0x1000000,0x208,0x0,0x0,0x0,0x0},{0x5,0x14000000,0x152,0xE000000,0x1C70,0xC00000,0xC489,0x0,0x0},{0x3,0xC000000,0xB1,0x7000000,0xE38,0x10600000,0x6244,0x0,0x0},{0x1,0xC000000,0x30,0x1000000,0x208,0x0,0x0,0x0,0x0}};
	public static final int[][][] CURVE_BB={{{0xD,0x4000000,0x418,0x1F000000,0x7FF9,0x6C00000,0x6E8D1,0x10480000,0x252364},{0xC,0x4000000,0x418,0x1F000000,0x7FF9,0x6C00000,0x6E8D1,0x10480000,0x252364},{0xC,0x4000000,0x418,0x1F000000,0x7FF9,0x6C00000,0x6E8D1,0x10480000,0x252364},{0x2,0x8000000,0x20,0x0,0x0,0x0,0x0,0x0,0x0}},{{0x1,0x8000000,0x20,0x0,0x0,0x0,0x0,0x0,0x0},{0xC,0x4000000,0x418,0x1F000000,0x7FF9,0x6C00000,0x6E8D1,0x10480000,0x252364},{0xD,0x4000000,0x418,0x1F000000,0x7FF9,0x6C00000,0x6E8D1,0x10480000,0x252364},{0xC,0x4000000,0x418,0x1F000000,0x7FF9,0x6C00000,0x6E8D1,0x10480000,0x252364}},{{0x2,0x8000000,0x20,0x0,0x0,0x0,0x0,0x0,0x0},{0x1,0x8000000,0x20,0x0,0x0,0x0,0x0,0x0,0x0},{0x1,0x8000000,0x20,0x0,0x0,0x0,0x0,0x0,0x0},{0x1,0x8000000,0x20,0x0,0x0,0x0,0x0,0x0,0x0}},{{0x2,0x4000000,0x10,0x0,0x0,0x0,0x0,0x0,0x0},{0x2,0x10000000,0x40,0x0,0x0,0x0,0x0,0x0,0x0},{0xA,0x0,0x408,0x1F000000,0x7FF9,0x6C00000,0x6E8D1,0x10480000,0x252364},{0x2,0x4000000,0x10,0x0,0x0,0x0,0x0,0x0,0x0}}};

	@Override
	public int getCurveType() {
		return CURVETYPE;
	}
	@Override
	public int getCurveA() {
		return CURVE_A;
	}
	@Override
	public int[] getCurveB() {
		return CURVE_B;
	}
	@Override
	public int[] getCurveOrder() {
		return CURVE_Order;
	}
	@Override
	public int[] getCurveBnx() {
		return CURVE_Bnx;
	}
	@Override
	public int[] getCurveCru() {
		return CURVE_Cru;
	}
	@Override
	public int[] getCurveFra() {
		return CURVE_Fra;
	}
	@Override
	public int[] getCurveFrb() {
		return CURVE_Frb;
	}
	@Override
	public int[] getCurvePxa() {
		return CURVE_Pxa;
	}
	@Override
	public int[] getCurvePxb() {
		return CURVE_Pxb;
	}
	@Override
	public int[] getCurvePya() {
		return CURVE_Pya;
	}
	@Override
	public int[] getCurvePyb() {
		return CURVE_Pyb;
	}
	@Override
	public int[] getCurveGx() {
		return CURVE_Gx;
	}
	@Override
	public int[] getCurveGy() {
		return CURVE_Gy;
	}
	@Override
	public int[][] getCurveW() {
		return CURVE_W;
	}
	@Override
	public int[][][] getCurveSB() {
		return CURVE_SB;
	}
	@Override
	public int[][] getCurveWB() {
		return CURVE_WB;
	}
	@Override
	public int[][][] getCurveBB() {
		return CURVE_BB;
	}

}
