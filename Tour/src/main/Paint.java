package main;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public class Paint { // 描画クラス
	public static int r = 10; // 描画する円の半径
	private Graphics gra;	 // 使用するグラフィックス
	private Graphics defGra;  // 初期のグラフィックス（フレームのグラフィックス）
	private final Image backMap; // 背景画像
	private Color gray = new Color(240,240,240); 
	
	public Paint(Graphics g, Image map) { 
		gra = g;
		defGra = g;
		backMap = map;
	}
	
	public void map(MapData[] map) { // マップの描画
		gra.setColor(Color.white); // 背景色でリセット
		gra.fillRect(0, 85, 1300, 800);
		
		if(backMap!=null) // 背景画像がれば表示
			gra.drawImage(backMap, 25,100,null);
		else 
			squareBack(); // なければマス目を表示
		
		if(map.length>0) { // マップデータが空でなければ
			gra.setColor(Color.blue); // スタート地点を描画
			gra.fillOval(map[0].x-(r+2), map[0].y-(r+2), (r+2)*2, (r+2)*2);
		}
		for(int i=0; i<map.length; i++) { // 全マップデータを描画
			circle(map[i].x, map[i].y);			
		}	
	}
	
	public void answer(MapData[] map, int[] perm) { // 試行結果を表示　マップデータと最小の並びが引数
		map(map); // マップを表示
		
		double c;
		Color color;
		for(int i=0; i<perm.length-1; i++) { // スタートから最後まで
			c = i*4 / (perm.length-2); // 全体に対して何番目の移動かを0~4の少数で表現
			if(c<=1) 		// 1/4までなら青→水色
				color = new Color(0,(int)(c*150),255);
			else if(c<=2) // 2/4までなら水色→緑
				color = new Color(0,150,(int)((2-c)*255));
			else if(c<=3) // 3/4までなら緑→黄色
				color = new Color((int)((c-2)*255),150,0);
			else 			// 4/4までなら黄色→赤
				color = new Color(255,(int)((4-c)*150),0);
			
			// 移動を線で表現
			((Graphics2D)gra).setStroke(new BasicStroke(4));
			gra.setColor(Color.black);	
			gra.drawLine(map[perm[i]].x, map[perm[i]].y, map[perm[i+1]].x, map[perm[i+1]].y);
			((Graphics2D)gra).setStroke(new BasicStroke(2));
			gra.setColor(color);	
			gra.drawLine(map[perm[i]].x, map[perm[i]].y, map[perm[i+1]].x, map[perm[i+1]].y);
		}
		// 最後からスタートへの移動
		((Graphics2D)gra).setStroke(new BasicStroke(4));
		gra.setColor(Color.black);	
		gra.drawLine(map[perm[0]].x, map[perm[0]].y, map[perm[perm.length-1]].x, map[perm[perm.length-1]].y);
		gra.setColor(new Color(255,0,30)); 
		((Graphics2D)gra).setStroke(new BasicStroke(2));
		gra.drawLine(map[perm[0]].x, map[perm[0]].y, map[perm[perm.length-1]].x, map[perm[perm.length-1]].y);
	}
	
	public void circle(int x, int y) {	// 中心座標を引数とする半径rの円を描画
		gra.setColor(Color.red);
		gra.fillOval(x-r, y-r, 2*r,2*r);
		gra.setColor(Color.black);
		gra.drawOval(x-r, y-r, 2*r,2*r);
	}
	
	public void squareBack() { // マス目の表示
		((Graphics2D)gra).setStroke(new BasicStroke(1));
		gra.setColor(gray); // 背景色
		gra.fillRect(25, 100, 1200, 600);
		gra.setColor(Color.black);
		for(int i=0; i<=40; i++) { // 縦線
			gra.drawLine(i*30+25, 100, i*30+25, 700);
		}
		for(int i=0; i<=20; i++) { // 横線
			gra.drawLine(25, i*30+100, 1225, i*30+100);
		}
	}
	
	public void changeR(int dir) { // 半径の変更　3~15
		r -= dir;
		if(r<3) r=3;
		if(r>15) r=15;
	}
	
	public void setG(Graphics g) { // 画像出力用グラフィックに変更
		gra = g;
		gra.translate(0,-75); // 操作パネルは画像に出力しないため座標を調整
		g.setColor(Color.white); // 背景色
		g.fillRect(0,0,1500,1000);
	}
	public void setG() { // 元に戻す
		gra = defGra;
	}
}
