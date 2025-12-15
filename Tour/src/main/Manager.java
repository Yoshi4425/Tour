package main;

import java.util.List;
import java.util.ArrayList;

public class Manager { // 管理者のクラス
	private List<MapData> mapList = new ArrayList<MapData>(); // マップデータのリスト
	private MapData[] map; // マップデータの配列　シミュレーションのときに使用
	private Paint paint; // 描画インスタンス
	private Simulator sim; // シミュレーションを行うインスタンス
	private int[] minPerm; // 最小値を記録した並び
	private boolean mode = true; //準備モードがtrue
	
	public Manager(Paint p) {
		paint = p;
	}
	
	public void simulate(int repeat) { // 引数の数だけシミュレーションを繰り返す
		if(mapList.size() < 3) return; // データ数が3未満の時は実行しない
		if(mode) { // 準備モードなら
			mode = false; // 実行モードに
			map = mapList.toArray(new MapData[mapList.size()]); // 配列にしてmapに記録
			minPerm = new int[map.length]; // 初期配置
			for(int i=0; i<minPerm.length; i++) {
				minPerm[i] = i;
			}
			sim = new Simulator(map, minPerm);	// シミュレータ作成
		}
		
		for(int i=0; i<repeat; i++) { // シミュレート繰り返し
			int[] p = sim.simulate(map); // 結果を受け取り
			if(p!=null) {// nullでなければ更新
				minPerm = p;
			}
		}
		
		sort(); // スタート地点が一番に来るように入れ替え
		paint.answer(map, minPerm); // 結果表示
	}
	
	public void auto() {	// 自動シミュレート 一定回数シミュレートし、一度も更新されなければ終了
		simulate(0);
		int[] nowPerm;
		do {
			nowPerm = minPerm;
			simulate(5000000);	// ここでminPermが更新されれば、nowPermとは異なる配列がminPermに入っている
		}while(nowPerm != minPerm);
	}
	
	public void add(int x, int y) { // 地点登録
		if(mode) { // 準備モードなら
			mapList.add(new MapData(x,y)); // リストに追加
			paint.map(mapList.toArray(new MapData[mapList.size()])); // 表示
		}
	}
	
	public void delete(int x, int y) { // 地点消去
		if(mode) {
			for(int i=mapList.size()-1; i>=0; i--) { // リストの後ろから
				if(mapList.get(i).onCircle(x,y)) { // クリックした位置が円上なら
					mapList.remove(i); // リストから消去
					break; // 終了
				}	
			}
			paint.map(mapList.toArray(new MapData[mapList.size()]));
		}
	}
	
	public void setStart(int x, int y) { // スタート地点変更
		if(mode) {
			for(int i=mapList.size()-1; i>=0; i--) { 
				if(mapList.get(i).onCircle(x,y)) {
					mapList.add(0,mapList.get(i)); // リストの先頭に追加
					mapList.remove(i+1); // 元の位置から消去
					break;
				}
			}
			paint.map(mapList.toArray(new MapData[mapList.size()]));
		}
	}
	
	private void sort() { // スタート地点から回るように調整
		if(minPerm[0] == 0) return;
		int index=0;
		while(minPerm[index] != 0) { // indexをスタート地点のインデックスに設定
			index++;
		}
		int[] newP = new int[minPerm.length];
		for(int i=0; i<minPerm.length; i++) { // 新しい配列に書き写す
			if(index == minPerm.length) // インデックスが配列の外に出たら0へ
				index = 0;
			newP[i] = minPerm[index]; // 元の配列[index]を新しい配列に書き写す
			index++;
		}
		minPerm = newP; // 変更を反映
	}
	
	public void paintMap() { // 画面表示
		if(mode) // 準備モードなら
			paint.map(mapList.toArray(new MapData[mapList.size()])); // 配列に変換してマップ表示
		else // 実行モードなら
			paint.answer(map, minPerm); // 結果を表示
	}
	
}
