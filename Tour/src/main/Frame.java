package main;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class Frame extends JFrame implements ActionListener, MouseListener, MouseWheelListener, WindowListener, PopupMenuListener{
	
	private static final long serialVersionUID = 1L;
	// ボタン、コンボボックス
	private JButton b1 = new JButton("Simulate");
	private JButton b2 = new JButton("Print");
	private String[] repeat = new String[]{"100", "1万", "100万", "1000万","Auto"};
	private JComboBox<String> cb = new JComboBox<String>();
	// インスタンスの変数
	private Manager manager;
	private Paint paint;
	private int repN = 100; // 繰り返し回数
	
	// メインプログラム
	public static void main(String[] args) {
		if(args.length==0) {
			new Frame("");
		}else {
			new Frame(args[0]);
		}
	}
	
	public Frame(String mapName) {
		super("Tour");                    // スーパクラスのコンストラクタ呼び出し
		
		Image map = null;
		int w = 1200;
		int h = 600;
		
		if((new File(mapName)).exists()) {	// ファイルが存在するなら
			map = Toolkit.getDefaultToolkit().getImage(mapName);	// 画像取得
			//画像読み込み
			MediaTracker tracker = new MediaTracker(this); 
			tracker.addImage(map,0);
			try {
				tracker.waitForAll();
			} catch (InterruptedException e) {
			}
			h = map.getHeight(null);	// 画像の縦横サイズを入手
			w = map.getWidth(null);
	
			if(w/h >= 2) { // 縦横比に応じて画像サイズの調整
				double ratio = (double)1200/w;
				w = 1200;
				h = (int)(ratio*h);
			}else {
				double ratio = (double)575/h;
				h = 575;
				w = (int)(ratio*w);
			}
			map = map.getScaledInstance(w, h, 0);	// 画像サイズの設定
			tracker.addImage(map,0); // 更新した画像の読み込み
			try {
				tracker.waitForAll();
			} catch (InterruptedException e) {
			}
		}
		//フレームの設定
		setSize(w+50, h+125);  	        
		setBackground(Color.white);      // 背景色を白とする
		Container cp = getContentPane();                // コンテナ設定
		cp.setBackground(getBackground());              // コンテナの背景設定
		cp.setLayout(new FlowLayout(FlowLayout.LEFT));  // 左側から並べるレイアウト
	
		// 操作ボタン生成
		JPanel pn = new JPanel();                       // パネル生成
		pn.setLayout(new FlowLayout(FlowLayout.LEFT));  // 左から並べるレイアウト
	    
	    cb.setEditable(false);               // コンボボックスの編集は不可とする
	    for (int i=0; i<repeat.length; i++) {   
			cb.addItem(repeat[i]);               
	    }
	    cb.addActionListener(this);  // コンボボックスにリスナ(アクション監視)付加
	    cb.addPopupMenuListener(this);// コンボボックスにリスナ(ポップアップ監視)付加
	    pn.add(cb);                 // パネルにコンボボックスを付加
	    
		JLabel lb = new JLabel("回  ");  
		lb.setForeground(Color.black);        // ラベルの文字色を黒とする
		pn.add(lb);                          // パネルにラベルを付加

		b1.addActionListener(this);     // ボタンにリスナ（アクション監視)付加
		pn.add(b1);                     // パネルにボタン追加
		cp.add(pn);                     // コンテナにパネル追加
		
		pn = new JPanel();                       // パネル生成
		pn.setLayout(new FlowLayout(FlowLayout.LEFT));  // 左から並べるレイアウト
		b2.addActionListener(this);     // ボタンにリスナ（アクション監視)付加
		pn.add(b2);                     // パネルにボタン追加
		cp.add(pn);                          // コンテナにパネル追加

		setVisible(true);        // フレーム可視化
		
		paint = new Paint(getGraphics(),map); // ペイントインスタンスと管理インスタンスを生成
		manager = new Manager(paint);
		
		addMouseListener(this);  // マウスリスナー（アクション監視）付加
		addMouseWheelListener(this); // マウスホイールリスナー（アクション監視）付加
		addWindowListener(this); // ウィンドウリスナー（アクション監視）付加
	}
	
	// アクションが発生した（メニューが選択された、ボタンが押された）場合の処理
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == b1) {   // Simulateボタン
			if(repN == -1)
				manager.auto();
			else
				manager.simulate(repN);
		}
		if (evt.getSource() == b2) {   // Printボタン
			makeImage();
		}
		if(evt.getSource() == cb) { // 回数選択のコンボボックス
		    String str = (String)cb.getSelectedItem();  // 選択された項目の文字列を得る
		    if(str == repeat[0])
		    	repN = 100;
		    if(str == repeat[1])
		    	repN = 10000;
		    if(str == repeat[2])
		    	repN = 1000000;
		    if(str == repeat[3])
		    	repN = 10000000;
		    if(str == repeat[4])
		    	repN = -1;
		}
	}
	
	public void mousePressed(MouseEvent evt) {
		int x = evt.getX();  // イベントが発生したところのＸ座標を記憶
		int y = evt.getY();  // イベントが発生したところのＹ座標を記憶

		if(evt.getButton()==MouseEvent.BUTTON1) {	// 左クリック
			if(x>=25 && x<= getWidth()-25 && y >= 100 && y<=getHeight()-25) // 操作範囲なら
				manager.add(x,y); // 座標の追加
		}else if(evt.getButton()==MouseEvent.BUTTON2) { // ホイールクリック
			manager.setStart(x,y); // スタート位置の設定
		}else if(evt.getButton()==MouseEvent.BUTTON3) { // 右クリック
			manager.delete(x,y);	// 座標の削除
		}
	}
	
	public void mouseWheelMoved(MouseWheelEvent evt) { // マウスホイール回転
		paint.changeR(evt.getWheelRotation());	// 円半径変更
		repaint();
	}
	
	public void popupMenuCanceled(PopupMenuEvent arg0) { // ポップアップメニューキャンセル
		repaint();
	}
	public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) { // ポップアップメニュー非表示
		repaint();
	}
	public void windowActivated(WindowEvent arg0) { // ウィンドウ起動時
		repaint();
	}
	public void windowClosing(WindowEvent arg0) { // ウィンドウ消去
		dispose();       // フレームを消す
		System.exit(0);  // 終了
	}
	
	//フレームを復元（再描画）する処理
	public void paint(Graphics gra) {
		super.paint(gra);    // スーパークラス（JFrame）の paint() 呼び出し
	    manager.paintMap();
	}
		
	public void makeImage() { // 画像書き出し
		
		BufferedImage writeImage = // 画像生成用のインスタンス
				new BufferedImage(getWidth(),getHeight()-75, BufferedImage.TYPE_INT_RGB);
		paint.setG(writeImage.getGraphics()); // 画像書き込み用のグラフィックをペイントに設定
		manager.paintMap(); // 書き込み
		paint.setG();	// ペイントの設定を元に戻す
		try {
		  ImageIO.write(writeImage, "PNG", new File("result.png")); // 画像出力
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// リスナーの使用していないメソッド
	public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {}
	public void mouseReleased(MouseEvent evt) {}
	public void mouseClicked(MouseEvent evt) {}
	public void mouseEntered(MouseEvent evt) {}
	public void mouseExited(MouseEvent evt) {}
	public void windowClosed(WindowEvent arg0) {}
	public void windowDeactivated(WindowEvent arg0) {}
	public void windowDeiconified(WindowEvent arg0) {}
	public void windowIconified(WindowEvent arg0) {}
	public void windowOpened(WindowEvent arg0) {}
}
