package main;

import java.util.Random;
import java.util.ArrayList;

public class Simulator { // シミュレータ
	private int[][] newGene; // 新世代
	private int[][] oldGene; // 旧世代
	private double[] adapt = new double[10]; // 適応値
	private double minAdapt; // 今まで一番小さい適応値
	private int spotN; // 地点数
	private Random rand = new Random(); // 乱数生成インスタンス
	
	public Simulator(MapData[] map, int[]defPerm) { // マップデータと初期の回り方
		spotN = map.length; 
		oldGene = new int[10][spotN];
		
		ArrayList<Integer> num = new ArrayList<Integer>(); // int型のリスト
		
		int i,j,index;
		for(i=0; i<10; i++) { // 旧世代の初期値設定
			for(j=0; j<spotN; j++) { // リストに数字を入れる
				num.add(j);
			}
			for(j=0; j<spotN; j++) { // 重複が起きないように
				index = rand.nextInt(num.size()); // 数字のリストから取り出す
				oldGene[i][j] = num.get(index); // リストから取り出しながら旧世代に値を移す
				num.remove(index);
			}
		}
		minAdapt = adaptiveValue(defPerm,map); // 適応値を計算
	}

	public int[] simulate(MapData[] map) { // シミュレートする
		newGene = new int[10][spotN]; // 配列を新しく生成
		int r;
		for(int i=0; i<10; i++) { // 新世代を10個生成
			r  = rand.nextInt(100); // 0~99で整数を生成 
			if(r < 88) { // 0~87
				newGene[i] = crossOver(); // 交叉
			}else if(r < 97) { // 88~96
				newGene[i] = reproduction(); // 再生
			}else { // 97~99
				newGene[i] = mutation(); // 突然変異
			}
			adapt[i] = adaptiveValue(newGene[i], map); // 生成した並びの適応値の計算
		}
		sort(); // 適応値によって新世代を並び替え
		oldGene = newGene; // 世代交代
	
		if(adapt[0] < minAdapt){ // 世代での最小値が今までの最小値より小さければ
			minAdapt = adapt[0]; // 最小値更新
			return oldGene[0]; // 最もいい並びを返す
		}else 
			return null; // 更新しなければnull
	}
	
	public int[] crossOver() { // 交叉　親1のある区間を保存し、残りは親2から並びを崩さずに補完する
		int i,p1,p2,r1,r2;
		do {
			p1 = weightedSelect(); // 親１
			p2 = weightedSelect(); // 親２
		}while(p1 == p2); // 一緒ならやり直し
		do { 
			r1 = rand.nextInt(spotN); // 区間の開始地点
			r2 = rand.nextInt(spotN); // 区間の終了地点
			if(r1>r2) { // 大小関係が逆なら入れ替え
				int tmp = r1;
				r1 = r2;
				r2 = tmp;
			}
		}while(r2==r1 || r2-r1+1<spotN/4 || r2-r1+1>spotN*3/4); //区間が短すぎたり長過ぎたらやり直し
		
		int[] perm = new int[spotN]; // 新しい並びを準備
		for(i=r1; i<=r2; i++) {
			perm[i] = oldGene[p1][i]; // 区間を親1からコピー
		}
		
		boolean flag; 
		int num,j=0,k;
		for(k=0; k<spotN; k++) {
			num = oldGene[p2][k]; // 親2の並びから値を得る
			flag = true;
			for(i=r1; i<=r2; i++) { // 親1の区間に数字が含まれていれば
				if(num == oldGene[p1][i])
					flag = false; // フラグを下げる
			}
			if(j==r1) // 新しい並びのインデックスが区間に入ったら
				j = r2+1; // 区間の終わりに移動
			
			if(flag) { // フラグが立っていれば
				perm[j] = num; // 新しい並びに値をセット
				j++; // 新しい並びのインデックスを進める
			}
		}
		return perm; // 新しい配列を返す
	}
	
	private int[] reproduction() { // 再生　旧世代の並びを選んで返す
		return oldGene[weightedSelect()];
	}
	
	private int[] mutation() { // 突然変異　親を一つ選び、区間の間の並びをランダムに入れ替えてから返す
		int[] perm = new int[spotN]; // 新しい並び
		int[] parent = oldGene[weightedSelect()]; // 親
		int r1,r2;
		do { // 短すぎない区間を設定
			r1 = rand.nextInt(spotN);
			r2 = rand.nextInt(spotN);
			if(r1>r2) {
				int tmp = r1;
				r1 = r2;
				r2 = tmp;
			}
		}while(r2==r1 || r2-r1+1<=spotN/3);
		
		for(int i=0; i<spotN; i++) { // 親の並びをコピー
			perm[i] = parent[i];
		}
		int a1,a2,tmp;
		for(int i=0; i<spotN*2; i++) { // 地点数の二倍繰り返す
			a1 = rand.nextInt(r2-r1+1); // 入れ替え箇所1
			a2 = rand.nextInt(r2-r1+1); // 入れ替え箇所2
			// 入れ替え
			tmp = perm[r1+a1];
			perm[r1+a1] = perm[r1+a2];
			perm[r1+a2] = tmp;
		}
		return perm;
	}
	
	private void sort() { // 適応値による新世代の並び替え
		  int k,j,index;
		  double tmpA;	// 適応値の一時変数
		  int[] tmpG;		// 並びの一時変数
		  //単純入れ替えソート
		  for(k=0; k<9; k++){
			index = k;
			for(j=k+1; j<10; j++){
			  if(adapt[j] < adapt[index]){
				index = j;
			  }
			}
			// 適応地の入れ替え
			tmpA = adapt[k];
			adapt[k] = adapt[index];
			adapt[index] = tmpA;
			// 並びの入れ替え
			tmpG = newGene[k];
			newGene[k] = newGene[index];
			newGene[index] = tmpG;
		  }
		  return;
	}
	
	private int weightedSelect() { // 適応地のランクによって確率 を返る
		int r = rand.nextInt(100);
		if(r<20) // 20%
			return 0;
		else if(r<37	) // 17%
			return 1;
		else if(r<53) // 16%
			return 2;
		else if(r<68) // 15%
			return 3;
		else if(r<79) // 11%
			return 4;
		else if(r<87) // 8%
			return 5;
		else if(r<93) // 6%
			return 6;
		else if(r<97) // 4%
			return 7;
		else if(r<99) // 2%
			return 8;
		else   		// 1%
			return 9;
	}
	
	public double adaptiveValue(int[] perm, MapData[] map) {	//適応値の計算
		int x, y;
		double sum = 0;
		for(int i=0; i<perm.length-1; i++) { // スタート→最後 までの距離を一つずつ足していく
			x = map[perm[i]].x - map[perm[i+1]].x;
			y = map[perm[i]].y - map[perm[i+1]].y;
			sum += Math.sqrt(x*x + y*y);
		}
		// 最後→スタートの距離を足す
		x = map[perm[0]].x - map[perm[spotN-1]].x;
		y = map[perm[0]].y - map[perm[spotN-1]].y;
		sum += Math.sqrt(x*x + y*y);
		return sum;
	}
	
}
