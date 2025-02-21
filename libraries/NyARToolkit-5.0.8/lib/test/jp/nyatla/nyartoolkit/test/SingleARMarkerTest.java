/* 
 * PROJECT: NyARToolkit(Extension)
 * --------------------------------------------------------------------------------
 *
 * The NyARToolkit is Java edition ARToolKit class library.
 * Copyright (C)2008-2012 Ryo Iizuka
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as publishe
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * For further information please contact.
 *	http://nyatla.jp/nyatoolkit/
 *	<airmail(at)ebony.plala.or.jp> or <nyatla(at)nyatla.jp>
 * 
 */
package jp.nyatla.nyartoolkit.test;

import java.io.File;
import java.io.FileInputStream;

import jp.nyatla.nyartoolkit.core.NyARRuntimeException;
import jp.nyatla.nyartoolkit.core.marker.artk.NyARCode;
import jp.nyatla.nyartoolkit.core.param.NyARParam;
import jp.nyatla.nyartoolkit.core.raster.rgb.INyARRgbRaster;
import jp.nyatla.nyartoolkit.core.raster.rgb.NyARRgbRaster;
import jp.nyatla.nyartoolkit.core.rasterdriver.squaredetect.NyARSquare;
import jp.nyatla.nyartoolkit.core.types.NyARBufferType;
import jp.nyatla.nyartoolkit.core.types.matrix.NyARDoubleMatrix44;
import jp.nyatla.nyartoolkit.old.processor.SingleARMarkerProcesser;
/**
 * このプログラムは、NyIdマーカ検出クラス{@link SingleARMarkerProcesser}の動作チェックプログラムです。
 * 静止画から1個のhiroマーカを認識する動作を確認できます。
 * 
 * このプログラムには結果を表示する機能がありません。
 * 数値の確認は、ブレークポイントを仕掛けるなどして行ってください。
 * <p>必要なファイル - 
 * このプログラムの実行には、以下の外部ファイルが必要です。
 * <ul>
 * <li>camera_para.dat - ARToolKit付属のカメラパラメータファイル
 * <li>patt.hiro - ARToolKit付属のHiroマーカのパターンファイル
 * <li>320x240ABGR.raw　- Hiroマーカを撮影した、QVGAサイズのXBGR形式のサンプル画像
 * </ul>
 * </p>
 */
public class SingleARMarkerTest
{
	class MarkerProcessor extends SingleARMarkerProcesser
	{	
		private Object _sync_object=new Object();
		public NyARDoubleMatrix44 transmat=null;
		public int current_code=-1;
	    /**
         * コンストラクタです。
         * パラメータを{@link #initInstance}へセットして初期化します。
         * ここでは、{@link #initInstance}へ値を引き渡すだけです。
         * @param i_cparam
         * カメラパラメータ。
         * @param i_raster_format
         * 入力ラスタのフォーマット。
         * @throws Exception
         */
		public MarkerProcessor(NyARParam i_cparam,int i_raster_format)
		{
			//アプリケーションフレームワークの初期化
			super();
			initInstance(i_cparam);
			return;
		}
        /**
         * この関数は、{@link #detectMarker}から呼び出される自己コールバック関数です。
         * 画像にマーカが現われたときに呼び出されます。
         * ここでは、例として、マーカのインデクス番号を保存する処理をしています。
         */		
		protected void onEnterHandler(int i_code)
		{
			synchronized(this._sync_object){
				current_code=i_code;
			}
			System.out.println("Marker Number:"+i_code);
		}
        /**
         * この関数は、{@link #detectMarker}から呼び出される自己コールバック関数です。
         * 画像からマーカが消え去った時に呼び出されます。
         * ここでは、マーカが消えた場合の後始末処理をします。
         * このサンプルでは、メンバ変数をリセットしています。
         */		
		protected void onLeaveHandler()
		{
			synchronized(this._sync_object){
				current_code=-1;
				this.transmat=null;
			}
			return;			
		}
        /**
         * この関数は、{@link #detectMarker}から呼び出される自己コールバック関数です。
         * 画像中のマーカの位置が変化したときに呼び出されます。
         * この関数は、{@link #onEnterHandler}直後に呼び出されることもあります。
         * 
         * このサンプルでは、引数で通知されたマーカの姿勢を、メンバ変数に保存しています。
         */
		protected void onUpdateHandler(NyARSquare i_square, NyARDoubleMatrix44 result)
		{
			synchronized(this._sync_object){
				this.transmat=result;
			}			
		}
	}
	private final static String CARCODE_FILE = "../Data/patt.hiro";
	private final static String PARAM_FILE = "../Data/camera_para.dat";	
	private final String data_file = "../Data/320x240ABGR.raw";
	/**
	 * コンストラクタです。
	 * ここで行う処理はありません。
	 */
	public SingleARMarkerTest()
    {
    }
    /**
     * テスト関数の本体です。
     * 設定ファイル、サンプル画像の読み込んだのちに、１種類のマーカを登録した{@link MarkerProcessor}を生成し、
     * １回だけ画像を入力して、マーカ検出を試行します。
     * @throws Exception
     */	
    public void Test() throws Exception
    {
        //AR用カメラパラメタファイルをロード
        NyARParam ap = NyARParam.loadFromARParamFile(new FileInputStream(PARAM_FILE),320,240);

		// 試験イメージの読み出し(320x240 BGRAのRAWデータ)
		File f = new File(data_file);
		FileInputStream fs = new FileInputStream(data_file);
		byte[] buf = new byte[(int) f.length()];
		fs.read(buf);		

        INyARRgbRaster ra = NyARRgbRaster.createInstance(320, 240,NyARBufferType.BYTE1D_B8G8R8X8_32,false);
        ra.wrapBuffer(buf);

        MarkerProcessor pr = new MarkerProcessor(ap, ra.getBufferType());
        NyARCode[] codes=new NyARCode[1];
        codes[0]=NyARCode.loadFromARPattFile(new FileInputStream(CARCODE_FILE),16,16);
        pr.setARCodeTable(codes,16,80.0);
        pr.detectMarker(ra);
        return;
    }
    /**
     * プログラムのエントリーポイントです。
     * サンプルプログラム{@link NyIdTest}を実行します。
     * @param args
     * 引数はありません。
     */    
	public static void main(String[] args)
	{
		try {
			SingleARMarkerTest t = new SingleARMarkerTest();
			// t.Test_arGetVersion();
			t.Test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}    
}
