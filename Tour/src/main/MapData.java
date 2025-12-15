
package main;

public class MapData { // 座標のデータ
	public final int x;
	public final int y;
	
	public MapData(int xx, int yy) {
		x = xx;
		y = yy;
	}
	
	public boolean onCircle(int px, int py) { // (px,py)が円の上にあるかを確認
		int lx = x - px;
		int ly = y - py;
		System.out.println(x+","+y);
		return (lx*lx + ly*ly < Paint.r*Paint.r);
	}
}
