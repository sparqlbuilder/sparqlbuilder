
// API用プレフィックス指定
var prefix = 'http://www.sparqlbuilder.org';


// 描画系定数
var PATHNUM = 0;
var MAXDEPTH = 0;
var TREESPACE = 0;
var NODEHEIGHT = 50;
var DRAWHEIGHT = 0;

// パス探索に使うエンドポイント・スタートクラス・エンドクラス
var endpoint = "";
var startclass= "";
var endclass = "";

// GETメソッドで指定されていた場合の値取得用
var defendpoint = "";
var defstartclass= "";
var defendclass = "";

// 取得したjson保存用
var jsontext = "";
// 描画に利用するオブジェクト
var pathobj = "";

// 初めに表示するパス数の上限
var pathlimit = 10;


// ページ読み込みが完了次第実行
$(function(){

	// ページのパーツ部分を追加
	initparts();

	// GETメソッドで指定された値があれば取得
	getParameter();

	// 指定があれば
	if(defendpoint != "" && defstartclass != "" && defendclass != ""){
		// 指定された内容を使いSPARQLbuilderを開く
		openSPARQLBuilder();
	}

});

// パーツの初期化
function initparts(){
	// SPARQLbuilder用要素を取得
	var sbdiv = $('div#SPARQLBUILDER');
	// 中にdiv要素がなければ
	if(sbdiv.find('div').length == 0){
		// トップのSPARQLbuilder画面を作成
		var sbtop = '<div class="SBTopItems"><input type="button" class="SBTopButton" value="Open SPARQLBuilder" onclick="openSPARQLBuilder()"><input type="button" class="SBTopButton" value="Try with Sample" onclick="openSample(\'http://www.ebi.ac.uk/rdf/services/reactome/sparql\', \'http://www.biopax.org/release/biopax-level3.owl#Protein\', \'http://www.biopax.org/release/biopax-level3.owl#Pathway\')"><br><textarea class="SBSparqlArea" rows="10"></textarea><br><input type="button" class="SBTopButton" value="Send SPARQL" onclick="sendSPARQL()"><input type="button" class="SBTopButton" value="Download Result" onclick="downloadResult()"></div><div class="SBTutorialLink">Tutorial for SPARQL Builder GUI is <a href="http://www.sparqlbuilder.org/doc/how-to-use-sparql-builder-gui/" target="_blank">here</a>.</div>';
		// パス表示画面のモーダルを作成
		var sbmodal = '<div class="SBModalView"><div class="SBModalContents"><div class="SBForms"><div class="SBSelects"><select class="SBEndPointSelect"></select><select class="SBStartClassSelect"></select><select class="SBEndClassSelect"></select></div><div class="SBPermaLink"><input type="button" class="SBPermaLinkButton" value="Permalink" onclick="openPermalink()" disabled="disabled"></div></div><div class="SBMessage"><div class="SBResult"><span class="SBPathnum"></span> Path<span class="SBPlural"></span> found.</div><input type="button" class="SBViewAll" value="View All Path" onclick="viewAll()"></div><div class="SBGraph"><div class="SBAjaxLoad" style="display: none;"><div class="SBLoadIcon"><img src="images/ajax-loader.gif"></div></div></div><div class="SBPath"><div class="SBSelectedPath"></div></div><div class="SBModalButtons"><input type="button" class="SBModalButton" value="Close" onclick="closeSPARQLBuilder()"></div></div></div>';

		// SPARQLbuilder用要素にトップ用画面を追加
		sbdiv.html(sbtop);
		// body要素末尾にモーダル画面を追加
		$('body').append(sbmodal);
	}

	// モーダル画面のクリックイベント
	$('.SBModalView').click(function(){
		// モーダル画面をフェードアウトし非表示に
		$(this).fadeOut(700);
	});

	// モーダル内のコンテンツ部分のクリックイベント
	$('.SBModalContents').click(function(event){
		// プロパゲーションを止めコンテンツ部分のクリックではフェードアウトしないようにする
		event.stopPropagation();
	});

	// エンドポイントリストの取得
	loadEndPointList();

	// エンドポイントが変更されたら
	$(".SBEndPointSelect").change(function() {
		// デフォルトでなければ
		if($(".SBEndPointSelect").val() != "SBDefault"){
			// 選択されているエンドポイントのURLを取得
			endpoint = $(".SBEndPointSelect").val();
			// スタートクラスを再読み込み
			loadStartClassList();
		}
	});

	// スタートクラスが変更されたら
	$(".SBStartClassSelect").change(function() {
		// デフォルトでなければ
		if($(".SBStartClassSelect").val() != "SBDefault"){
			// 選択されているスタートクラスのURIを取得
			startClass = $(".SBStartClassSelect").val();
			// エンドクラスを再読み込み
			loadEndClassList();
		}
	});

	// エンドクラスが変更されたら
	$(".SBEndClassSelect").change(function() {
		// デフォルトでなければ
		if($(".SBEndClassSelect").val() != "SBDefault"){
			// 選択されているエンドクラスのURIを取得
			endClass = $(".SBEndClassSelect").val();
			// 取得した情報を送りパスを再読み込み
			loadPathList();
		}
	});

}

// GETメソッドで送られたパラメータのチェック
function getParameter(){
	if( 1 < window.location.search.length ){
		var query = window.location.search.substring( 1 );
		var parameters = query.split( '&' );

		for( var i = 0; i < parameters.length; i++ ){
			var element = parameters[ i ].split( '=' );
			if(decodeURIComponent( element[ 0 ] ) == "ep"){
				defendpoint = decodeURIComponent( element[ 1 ] )
			}else if(decodeURIComponent( element[ 0 ] ) == "st"){
				defstartclass = decodeURIComponent( element[ 1 ] )
			}else if(decodeURIComponent( element[ 0 ] ) == "en"){
				defendclass = decodeURIComponent( element[ 1 ] )
			}
		}
	}
}


function openSPARQLBuilder(){

	$('.SBModalView').css('top', $(window).scrollTop()).css('height', window.innerHeight).fadeIn();

	resizeModalView();

	$(".SBEndPointSelect").select2();
	$(".SBStartClassSelect").select2();
	$(".SBEndClassSelect").select2();

	if(defendpoint != "" && defstartclass != "" && defendclass != ""){

		$('.SBStartClassSelect').on('lsccomplete', function(){
			$('.SBStartClassSelect').val(defstartclass);
			defstartclass = "";

			$(".SBEndPointSelect").select2();
			$(".SBStartClassSelect").select2();
			$(".SBEndClassSelect").select2();

			$('.SBStartClassSelect').unbind('lsccomplete');
			loadEndClassList();
		});

		$('.SBEndClassSelect').on('leccomplete', function(){
			$('.SBEndClassSelect').val(defendclass);
			defendclass = "";

			$(".SBEndPointSelect").select2();
			$(".SBStartClassSelect").select2();
			$(".SBEndClassSelect").select2();

			$('.SBEndClassSelect').unbind('leccomplete');
		});

		loadPathList();

		var eplist = $('.SBEndPointSelect option');

		if(eplist.length == 0){
			$('.SBEndPointSelect').on('epcomplete', function(){
				$('.SBEndPointSelect option').each(function(){
					if($(this).text() == defendpoint){
						$(this).attr('selected', 'selected');
					}
				});
				defendpoint = "";
				loadStartClassList();
				$('.SBEndPointSelect').unbind('epcomplete');
			});
		}else{
			$('.SBEndPointSelect option').each(function(){
				if($(this).text() == defendpoint){
					$(this).attr('selected', 'selected');
				}
			});
			defendpoint = "";
			loadStartClassList();
		}
	}
}

function resizeModalView(){

	if($('.SBModalView').css('display') == 'block'){
		var mvw = $('.SBModalContents').width();
		var mvh = $('.SBModalContents').height();
		$('.SBModalContents .SBForms').css('width', (mvw - 201) + 'px').css('height', 56 + 'px');
		$('.SBModalContents .SBMessage').css('width', 200 + 'px').css('height', 56 + 'px');
		$('.SBModalContents .SBGraph').css('width', (mvw - 201) + 'px').css('height', (mvh - 57) + 'px');
		$('.SBModalContents .SBPath').css('width', 180 + 'px').css('height', (mvh - 77 - 26) + 'px');
		$('.SBModalContents .SBModalButtons').css('width', 200 + 'px').css('height', '26px');

		var formw = $('.SBModalContents .SBForms').width();
		var selw = Math.floor(formw - 120);

		if(selw % 2 == 1){
			selw--;
		}
		$('.SBModalContents .SBSelects').css('width', selw);
		$('.SBModalContents .SBPermaLink').css('width', Math.floor(formw - selw));
	}
}

function openSample(ep, st, en){
	defendpoint = ep;
	defstartclass = st;
	defendclass = en;

	openSPARQLBuilder();
}

function openPermalink(){
	var baseurl = location.href;
	var spliturl = baseurl.split('?');
	var url = spliturl[0] + "?ep=" + encodeURIComponent(endpoint) + "&st=" + encodeURIComponent(startclass) + "&en=" + encodeURIComponent(endclass);

	window.open(url);
}

function closeSPARQLBuilder(){
	$('.SBModalView').fadeOut();
}

function switchLoadIcon(mode) {
	if(mode == "view"){
		$('.SBAjaxLoad').show();
	}else{
		$('.SBAjaxLoad').hide();
	}
};


// エンドポイントリストの取得
function loadEndPointList(){
	// パーマリンクボタンを無効化
	$('.SBPermaLinkButton').attr('disabled', true);
	// 情報取得用APIのURLを作成（プレフィックス＋eplist）
	// ds=trueによってデータセットモードになりエンドポイントのタイトルとURLが組で取得される
	var url = prefix + '/api/eplist?ds=true';

	// エンドポイント・スタートクラス・エンドクラスをそれぞれ空にし無効化
	$(".SBEndPointSelect").empty();
	$(".SBEndPointSelect").attr("disabled", "disabled");
	$(".SBStartClassSelect").empty();
	$(".SBStartClassSelect").attr("disabled", "disabled");
	$(".SBEndClassSelect").empty();
	$(".SBEndClassSelect").attr("disabled", "disabled");

	// ajaxで情報取得
	$.ajax({
		// urlをセット
		url: url,
		// 情報が取得できたら
		success: function(data) {
			// 取得したjsonのテキストをオブジェクトに変換
			var list = eval(data);
			// エンドポイントリストを空に
			$(".SBEndPointSelect").empty();
			// デフォルトを追加
			$(".SBEndPointSelect").append('<option value="SBDefault">SELECT Endpoint</option>');
			// 取得したリストの数だけ繰り返し
			for (var i = 0; i < list.length; ++i) {
				// 取得したurlとラベルをセットしoption要素を追加
				$(".SBEndPointSelect").append('<option value="' + list[i]['uri'] + '">' + list[i]['label'] + '</option>');
			}
			// エンドポイントが指定済みでなければ
			if(defendpoint == ''){
				// ロードアイコンを非表示
				switchLoadIcon("hide");
			}
			// モーダルビューが表示状態なら
			if($('.SBModalView').attr('display') == 'block'){
				// 各セレクトボックスを検索可能に
				$(".SBEndPointSelect").select2();
				$(".SBStartClassSelect").select2();
				$(".SBEndClassSelect").select2();
			}
			// エンドポイントの選択を有効化
			$(".SBEndPointSelect").removeAttr("disabled");
			// エンドポイントの読み込み完了を通知
			$(".SBEndPointSelect").trigger(new $.Event('epcomplete'));
		},
	});
}

// スタートクラスの取得
function loadStartClassList() {
	// パーマリンクボタンを無効化
	$('.SBPermaLinkButton').attr('disabled', true);
	// 情報取得用APIのURLを作成（プレフィックス＋clist＋選択されたエンドポイントURL）
	var url = prefix + "/api/clist?ep=" + encodeURIComponent(endpoint);
	// スタートクラス・エンドクラスをそれぞれ空にし無効化
	$(".SBStartClassSelect").empty();
	$(".SBStartClassSelect").attr("disabled", "disabled");
	$(".SBEndClassSelect").empty();
	$(".SBEndClassSelect").attr("disabled", "disabled");
	// ajaxで情報取得
	$.ajax({
		// メソッドタイプ指定
		type : "GET",
		// urlをセット
		url : url,
		// 情報が取得できたら
		success : function(data) {
			// 取得したjsonのテキストをオブジェクトに変換
			var list = eval(data);
			// スタートクラスを空に
			$(".SBStartClassSelect").empty();
			// デフォルトを追加
			$(".SBStartClassSelect").append('<option value="SBDefault">SELECT StartClass (From ' + list.length + ' classes below)</option>');
			// 取得したリストの数だけ繰り返し
			for (var i = 0; i < list.length; ++i) {
				// 取得したuri・ラベル・クラス数をセットしoption要素を追加
				$(".SBStartClassSelect").append('<option value="' + list[i]['uri'] + '">' + list[i]['label'] + ' (' + list[i]['number'] + ')' + '</option>');
			}
			// スタートクラスの選択を有効化
			$(".SBStartClassSelect").removeAttr("disabled");
			// スタートクラス・エンドクラスを検索可能に
			$(".SBStartClassSelect").select2();
			$(".SBEndClassSelect").select2();
			// スタートクラスの読み込み完了を通知
			$(".SBStartClassSelect").trigger(new $.Event('lsccomplete'));
		}
	});
};

// エンドクラスの取得
function loadEndClassList() {
	// パーマリンクボタンを無効化
	$('.SBPermaLinkButton').attr('disabled', true);
	// 情報取得用APIのURLを作成（プレフィックス＋clist＋選択されたエンドポイント＋選択されたスタートクラス）
	var url = prefix + "/api/clist?ep=" + encodeURIComponent(endpoint) + '&class=' + encodeURIComponent(startClass);
	// ajaxで情報取得
	$.ajax({
		// メソッドタイプ指定
		type : "GET",
		// urlをセット
		url : url,
		// 情報が取得出来たら
		success : function(data) {
			// 取得したjsonのテキストをオブジェクトに変換
			var list = eval(data);
			// エンドポイントを空に
			$(".SBEndClassSelect").empty();
			// デフォルトを追加
			$(".SBEndClassSelect").append('<option value="SBDefault">SELECT EndClass (From ' + list.length + ' classes below)</option>');
			// 取得したリストの数だけ繰り返し
			for (var i = 0; i < list.length; ++i) {
				// 取得したuri・ラベル・クラス数をセットしoption要素を追加
				$(".SBEndClassSelect").append('<option value="' + list[i]['uri'] + '">' + list[i]['label'] + ' (' + list[i]['number'] + ')' + '</option>');
			}
			// エンドクラスの選択を有効化
			$(".SBEndClassSelect").removeAttr("disabled");
			// スタートクラス・エンドクラスを検索可能に
			$(".SBStartClassSelect").select2();
			$(".SBEndClassSelect").select2();
			// エンドクラスの読み込み完了を通知
			$(".SBEndClassSelect").trigger(new $.Event('leccomplete'));
		}
	});
};

// パスリストの取得
function loadPathList() {
	// スタートクラス・エンドクラスを取得
	startclass = $(".SBStartClassSelect").val();
	endclass = $(".SBEndClassSelect").val();

	// GETメソッドでそれぞれ指定されていれば上書き
	if(defendpoint != "" && defstartclass != "" && defendclass != ""){
		endpoint = defendpoint;
		startclass = defstartclass;
		endclass = defendclass;
	}

	// パーマリンクボタンを無効化
	$('.SBPermaLinkButton').attr('disabled', true);
	// URIが空やデフォルトなら終了
	if (startclass == null || endclass == null || startclass == "SBDefault" || endclass == "SBDefault"){
		return;
	}

// 要再検討
	// パス数上限をリセット
	pathlimit = 10;

	// 各種初期化
	$('.SBResult').hide();
	$('.SBViewAll').hide();
	$('.SBSelectedPath').html('<h1>Please <span style="color: hsl(150, 50%, 75%);">select a leaf node</span> and click to generate a SPARQL</h1><img src=\"images/pathline.png\" style="display:none;">');

	// 情報取得用APIのURLを作成（プレフィックス＋plist＋各種選択結果）
	var url = prefix + "/api/plist?ep=" + encodeURIComponent(endpoint)
									+ "&startclass=" + encodeURIComponent(startclass)
									+ "&endclass="   + encodeURIComponent(endclass);

	// 読み込みアイコンを表示
	switchLoadIcon("view");
	// GETメソッドで指定された場合表示に問題が起きるので一瞬待ってから実行
	setTimeout(function(){
		// ajaxで情報取得
		$.ajax({
			// メソッドタイプ指定
			type : "GET",
			// urlをセット
			url : url,
			// タイムアウトをセット
			timeout : 1000000,
			// 情報が取得できたら
			success : function(data) {
				// 結果テキストをセット
				jsontext = data;
				// パスの描画
				view_map();
				// 読み込みアイコンを非表示
				switchLoadIcon("hide");
				// パーマリンクボタンを有効化
				$('.SBPermaLinkButton').attr('disabled', false);
			},
			// 取得に失敗したら
			error: function(data){
				// 読み込みアイコンを非表示
				switchLoadIcon("hide");
				// エラー内容をアラート
				alert("error: ", data);
			}
		});
	}, 100);
};

// 見つかったパスを全て描画
function viewAll(){
	// パス数上限を0に
	pathlimit = 0;
	// 再描画
	view_map();
}

// SPARQLの生成
generateSPARQL = function() {
	// 選択されたパスのオブジェクトをjson文字列に
    var path = JSON.stringify(pathobj);
	// 情報取得用APIのURLを作成（プレフィックス＋sparql＋パス情報）
    var url = prefix + '/api/sparql?path=' + encodeURIComponent(path);
	// ajaxで情報取得
    $.ajax({
		// メソッドタイプ指定
        type: "GET",
		// urlをセット
        url : url,
		// データタイプを指定
        dataType: 'text',
		// 情報が取得できたら
        success : function(data) {
			// 結果をSPARQL表示用エリアに挿入
            $(".SBSparqlArea").val(data);
			// SPARQLbuilderを閉じる
            closeSPARQLBuilder();
        }
    });
};

function sendSPARQL(){
	var sendep = $(".SBEndPointSelect").val();

	var query = $(".SBSparqlArea").val();

	if(sendep == "SBDefault" || query == ""){
		return;
	}

	query = encodeURIComponent(query);

	openpage = sendep + "?format=text%2Fhtml&query=" + query;

	window.open(openpage);
}

function downloadResult(){

	var sendep = $(".SBEndPointSelect").val();

	var query = $(".SBSparqlArea").val();

	if(sendep == "SBDefault" || query == ""){
		return;
	}

	qr = sendQuery(sendep,query);

	qr.fail(
		function (xhr, textStatus, thrownError) {
			alert("Error: A '" + textStatus+ "' occurred.");
		}
	);
	qr.done(
		function (d) {
			downloadCSV(d.results.bindings);
		}
	);
}

function downloadCSV(data){

	if (data instanceof Array) {
		var result_txt ="";

		var i=0;
		for ( var key in data[0]) {
			if(i>0){result_txt +=",";}
			result_txt += key;
			i++;
		}

		result_txt += "\n";

		for (var d = 0; d < data.length; d++) {
			var i = 0;
			for ( var key in data[d]) {
				if(i>0){result_txt +=",";}
				result_txt += data[d][key].value;
				i++;
			}
			result_txt += '\n';
		}

		var blob = new Blob( [result_txt], {type: 'text/plain'} )

		var link = document.createElement('a')
		link.href = URL.createObjectURL(blob)
		link.download = 'result' + '.csv'

		document.body.appendChild(link) // for Firefox
		link.click()
		document.body.removeChild(link) // for Firefox
	}
};


// グラフ描画部分
view_map = function(){

	// make_dataメソッドの結果を取得
	var json = make_data();

	// パス数が0でなければ
	if(json['nodes'].length != 0){

		// 出来上がった結果を渡してマップ上の座標をセット
		set_map_location(0, json['nodes'], json['links']);

		// SVGの幅と高さ用に描画領域のサイズを取得
		var width = $('.SBGraph').width();
		var height = $('.SBGraph').height();
		// SVG内のグラフ部分高さ（パス数に応じる）をセット
		var graphheight = ((NODEHEIGHT * 1.5) * PATHNUM) + (NODEHEIGHT / 2);

		// スコア表示のマージン
		var scoreleftmargin = NODEHEIGHT * 1.5;

		var scrollsvg = function(delta){
			// 現在のビューボックスの状態を取得
			var vb = svg.attr("viewBox");
			// スペースで区切り各値に分解
			var spvb = vb.split(" ");

			// ビューボックスのyの値から今回のホイールイベントの差分を引く
			var vby = (parseInt(spvb[1]) - parseInt(delta));

			// 0を割っていたら0に
			if(vby < 0){
				vby = 0;
			// スクロール上限（グラフサイズ引く表示領域サイズ）を超えていたら補正
			}else if(vby > (graphheight - height)){
				vby = (graphheight - height);
				// 補正した結果0を割っていたら0に
				if(vby < 0){
					vby = 0;
				}
			}

			// ここまででできたyをセットしビューボックスを更新
			svg.attr("viewBox", "0 " + vby + " " + width + " " + height);
		}

		// SVGの削除
		d3.select(".SBGraph svg").remove();
		// 画面サイズに合わせSVGの追加
		var svg = d3.select(".SBGraph").append("svg")
			.attr("width", width)
			.attr("height", height)
			// ビューボックスのセット
			.attr("viewBox", "0 0 " + width + " " + height);

		// SVGがスクロールされた時のイベントをブラウザに応じてセット
		var mousewheelevent = 'onwheel' in document ? 'wheel' : 'onmousewheel' in document ? 'mousewheel' : 'DOMMouseScroll';
		$(".SBGraph svg").on(mousewheelevent,function(e){
			// ブラウザに応じてスクロールの値を取得
			var delta = e.originalEvent.deltaY ? -(e.originalEvent.deltaY) : e.originalEvent.wheelDelta ? e.originalEvent.wheelDelta : -(e.originalEvent.detail);
			// FireFoxだとスクロール速度が非常に遅い場合があるので補正
			if(delta < 0 && delta > -100){
				delta = -100;
			}else if(0 < delta && delta < 100){
				delta = 100;
			}

			// スクロールのデフォルトの動作とバブリングをキャンセル
			e.preventDefault();
			e.stopPropagation();
			// 値を渡してスクロール
			scrollsvg(delta);
		});

		// 背景の追加（高さ以外は描画領域そのまま）
		var bg = svg
			.append("rect")
			.attr("x", 0)
			.attr("y", 0)
			.attr("width", width)
			.attr("height", function(){
				// グラフ部分の高さが描画領域の高さを割っていたら（パスが少なければ）描画領域の高さを返す
					if(graphheight < height){
						return height;
					}else{
						return graphheight;
					}
				})
			// 背景を薄いグレーに
			.attr("fill", "#fafafa");

		// links配列を渡しリンクの作成
		var link = svg.selectAll(".link")
			.data(json.links)
			.enter().append("line")
			.attr("class", "link")
			.style("stroke", "#999")
			.style("stroke-opacity", 0.6)
			.style("stroke-width", 2);

		// nodes配列を渡しノードの作成
		var node = svg.selectAll(".node")
			.data(json.nodes)
			.enter().append("circle")
			.attr("class", "node")
			.attr("r", (NODEHEIGHT / 2))
			.attr("cx", function(d) { return d.x;} )
			.attr("cy",  function(d) { return d.y; })
			.style("fill", function(d) { return d.nodecolor; })
			.style("stroke", '#fafafa')
			.style("stroke-width", '1.5px')
			// 末端ノードのみマウスアイコンをポインターに
			.style("cursor", function(d){
				if(d.path == "notend"){
					return 'normal';
				}else{
					return 'pointer';
				}
			});

		// nodes配列を渡しノードテキストの作成
		var tnode = svg.selectAll("text.node")
			.data(json.nodes)
			.enter().append("svg:text")
			.attr("class", "tnode")
			.attr("x", function(d) { return d.x; })
			.attr("y", function(d) { return d.y; })
			.text(function(d) { return d.name; })
			.style("fill", '#000000')
			.style("text-anchor", 'middle')
			.style("pointer-events", "none");

		// リンクテキストの作成
		var tlink = svg.selectAll("text.link")
			.data(json.links)
			.enter().append("svg:text")
			.attr("class", "tlink")
			.attr("x", function(d) { return (json.nodes[d.source].x + json.nodes[d.target].x) / 2; })
			.attr("y", function(d) { return (json.nodes[d.source].y + json.nodes[d.target].y) / 2; })
			.style("fill", '#000000')
			.style("text-anchor", 'middle');

		// スコアテキストの作成
		var tscore = svg.selectAll("text.score")
			.data(json.nodes)
			.enter().append("svg:text")
			.attr("class", "tscore")
			.attr("x", function(d) { return (d.x + scoreleftmargin); })
			.attr("y", function(d) { return d.y + 4; })
			.text(function(d) { return d.score; })
			.style("fill", 'hsl(0, 50%, 75%)')
			.style("text-anchor", 'middle')
			.style("pointer-events", "none");

		// ノードへのオンマウスでパス探索、パス中のリンク文字を表示
		node.on("mouseover", function(d){

			// 表示するパス保存用配列
			var path = [];
			// パス表示情報保存用配列
			var pathname = [];

			// まずオンマウスされたノードのidと名前をそれぞれ追加
			path.push(d.nodeid);
			pathname.push(d.name);

			// パス探索
			do{
				// リンクの数だけ繰り返し
				for(var i = 0; i < link.data().length; i++){
					// 現在の最後尾に繋がるリンクがあれば
					if(path[(path.length-1)] == link.data()[i].target){
						// そのリンクのソース側ノードのidを追加
						path.push(link.data()[i].source);
						// そのリンクの名前とソース側ノードの名前を追加
						pathname.push(link.data()[i].property);
						pathname.push(node.data()[link.data()[i].source].name);
					}
				}
			// ルートノードに辿り着くまで繰り返す
			}while(path[(path.length-1)] != 0);

			// ルートノードまたは途中ノードなら
			if(d.nodeid == 0 || d.path == "notend"){
				// パス表示領域をデフォルトに
				$('.SBSelectedPath').html('<h1>Please <span style="color: hsl(150, 50%, 75%);">select a leaf node</span> and click to generate a SPARQL</h1>');
			// 末端ノードなら
			}else{

				// パス表示領域用文字列
				var resultText = '';
				// パスの名前配列分後ろから繰り返しながら
				for (var i = pathname.length;i > 0; i--){
					// 奇数番目（ノード）なら
					if(i % 2 == 1){
						// ルートか途中か末端かに応じてクラスを指定し追記
						if(i == 1){
							resultText = resultText + "<div class=\"SBLeafNode\">" + pathname[i - 1] + "</div>";
						}else if(i == pathname.length){
							resultText = resultText + "<div class=\"SBRootNode\">" + pathname[i - 1] + "</div>";
						}else{
							resultText = resultText + "<div class=\"SBPathNode\">" + pathname[i - 1] + "</div>";
						}
					// 偶数番目（リンク）はリンク画像を前後につけ追記
					}else{
						resultText = resultText + "<img src=\"images/pathline.png\"><div class=\"SBPathProperty\">" + pathname[i - 1] + "</div><img src=\"images/pathline.png\">";
					}
				}

				// パス表示領域の内容を書き換え
				$('.SBSelectedPath').html(resultText);

				// サーブレットに送り返すパスオブジェクトを保存
				pathobj = d.path;

				// パス表示領域の表示設定をvisivleに
				$('.SBPath').css('overflow-y', 'visible');

				// パス内容の高さがパス表示領域を超えていたら
				if($('.SBPath').height() < $('.SBSelectedPath').innerHeight()){
					// パス表示領域の表示設定をスクロールに
					$('.SBPath').css('overflow-y', 'scroll');
				}
			}

			// オンマウスされたノードの高さに親を合わせるために合わせる高さを保存
			var movey = d.y;

			// 各ノードに対し
			node
				// 輪郭線の色を設定
				.style("stroke", function(d){
					// まずは背景色（デフォルト）を指定
					var strokecolor = "#fafafa";

					// パスのノード数だけ繰り返しながら
					for(var n = 0; n < path.length; n++){
						// パス内に含まれるノードだったら
						if(path[n] == d.nodeid){
							// 輪郭線を赤に
							strokecolor = "#ffaaaa";
						}
					}

					// ここまでで得られた輪郭線の色を返す
					return strokecolor;
				})
				// 高さの値
				.attr("cy", function(d){

					// 現在の高さを取得
					var currenty = d.y

					// パスのノード数だけ繰り返しながら
					for(var n = 0; n < path.length; n++){
						// パス内に含まれるノードだったら
						if(path[n] == d.nodeid){
							// 内部で持つ高さをオンマウスされたノードと同じに（再描画時に反映）
							d.y = movey;
						}
					}

					// 今は現時点の高さを返す
					return currenty;
				});

			// 各リンクテキストに対し
			tlink
				// テキスト表示判定
				.text(function(d) {
					// デフォルトで空をセット
					var linktext = "";

					// パスのノード数だけ繰り返しながら
					for(var t = 0; t < path.length; t++){
						// 自身がそのノードへ接続しているリンクならば
						if(path[t] == d.target){
							// リンクテキストにプロパティの値をセット
							linktext = d.property
						}
					}

					// ここまででできたリンクテキストを返す
					return linktext;
				});

			// 各リンクに対し
			link
				// 線の色判定
				.style("stroke", function(d){

					var strokecolor = "#999";

					// パスのノード数だけ繰り返しながら
					for(var t = 0; t < path.length; t++){
						// 自身がそのノードへ接続しているリンクならば
						if(path[t] == d.target){
							// リンクの色に赤をセット
							strokecolor = "#ffaaaa"
						}
					}

					// 線の色を返す
					return strokecolor;
				});

			// ここまでの設定を元に再描画
			redraw();

		// クリックされたとき
		}).on("click", function(d){
			// 末端ノードならスパークル発行
			if(d.path != "notend"){
				generateSPARQL();
			}
		});

		// 再描画関数
		var redraw = function (duration){

			// かける時間が未指定ならば
			if(duration == undefined){
				// 0.5秒かけてアニメーション
				duration = 500;
			}

			// 各リンクについて設定された位置に再描画
			link
				.transition()
				.duration(duration)
				.attr("x1", function(d) {return node.data()[d.source].x;})
				.attr("y1", function(d) {return node.data()[d.source].y;})
				.attr("x2", function(d) {return node.data()[d.target].x;})
				.attr("y2", function(d) {return node.data()[d.target].y;});

			// 各ノードについて設定された位置に再描画
			node
				.transition()
				.duration(duration)
				.attr("cx", function(d) {return d.x;})
				.attr("cy", function(d) {return d.y;});

			// 各ノードテキストについて設定された位置に再描画、テキスト描画位置を上下に振る
			tnode
				.transition()
				.duration(duration)
				.attr("x", function(d) {return d.x;})
				.attr("y", function(d) {
					// デフォルトで少し下げる
					var updown = (NODEHEIGHT * 0.4);
					// 奇数番目の深さなら少し上げる
					if(d.group % 2 == 1){
						updown = -(NODEHEIGHT * 0.2);
					}
					// その値を高さに返すことでテキスト描画位置が互い違いになる
					return d.y + updown;
				});

			// 各リンクテキストについて設定された位置に再描画
			tlink
				.transition()
				.duration(duration)
				.attr("x", function(d) {return (node.data()[d.source].x + node.data()[d.target].x) / 2;})
				.attr("y", function(d) {return ((node.data()[d.source].y + node.data()[d.target].y) / 2) + 4;});

		};

		// 初回のみdurationを0と指定し再描画（アニメーションなし）
		redraw(0);

	}else{
		// SVGの削除
		d3.select(".SBGraph svg").remove();
	}
};


// データ作成メソッド
make_data = function(){

	// 結果用オブジェクトを初期化
	ret = new Object();
	ret['nodes'] = new Array();
	ret['links'] = new Array();

	// 各種変数の初期化
	PATHNUM = 0;
	MAXDEPTH = 0;
	TREESPACE = 0;
	DRAWHEIGHT = NODEHEIGHT;

	// 表示するパス数
	var viewnum;

	// jsontextを取得
	var obj = jsontext;

	// 結果パス数のスタイルをリセット
	$('.SBResult').css('color', 'black').css('font-weight', 'normal').css('margin-top', '4px');

	// 複数形のsをつける
	$('.SBPlural').text('s');

	// パスの数が0だったら
	if(obj.length == 0){
		// 結果パス数のスタイルを赤の太字にし領域内上下中央に
		$('.SBResult').css('color', 'red').css('font-weight', 'bold').css('margin-top', '20px');
		// 複数形のsを削除
		$('.SBPlural').text('');
	// パス数が1なら
	}else if(obj.length == 1){
		// 複数形のsを削除
		$('.SBPlural').text('');
	}

	// パスの数が十以下なら
	if(obj.length <= 10){
		// 表示数をパス数に
		viewnum = obj.length;
		// 結果パス数のスタイルを領域内上下中央に
		$('.SBResult').css('margin-top', '20px');
		// 全表示ボタンを隠す
		$('.SBViewAll').hide();
	// リミットが10ならば
	}else if(pathlimit == 10){
		// 表示パス数を10に
		viewnum = 10;
		// 全表示ボタンを出す
		$('.SBViewAll').show();
	// リミットがなければ
	}else{
		// 表示パス数を全パス数に
		viewnum = obj.length;
		// 結果パス数のスタイルを領域内上下中央に
		$('.SBResult').css('margin-top', '20px');
		// 全表示ボタンを隠す
		$('.SBViewAll').hide();
	}

	// 結果パス数の値を更新
	$('.SBPathnum').text(obj.length);
	// 結果パス数を表示
	$('.SBResult').show();

	// objトップ階層の数だけ繰り返しながら
	for(var i = 0; i < viewnum; i++){
		if(i == 0){
			// 初回だけルートノードをプッシュ
			ret['nodes'].push({'name': obj[0]['label'], 'uri': obj[0]['startClass'], 'group': 0, 'x':50, 'y':50, 'nodeid':ret['nodes'].length, 'path': 'notend', 'nodecolor': 'hsl(40, 50%, 75%)'});
		}
		// 先にsourceに0（ルート）を代入
		var source = 0;
		// 共通ルート判定をtrueに
		var isCommon = true;

		// このパスのスコアを取得
		var score = obj[i]['score'];

		// classLinksの数だけ繰り返しながら
		for(var j = 0;j < obj[i]['classLinks'].length; j++){

			// リンクの名前をURL末尾から取得
			var propertytext = obj[i]['classLinks'][j]['predicate'];
			var propertysplit1 = propertytext.split("/");
			var propertysplit2 = propertysplit1[propertysplit1.length - 1];
			var propertysplit3 = propertysplit2.split("#");
			propertytext = propertysplit3[propertysplit3.length - 1];

			// 深さが最大より大きければ更新
			if(MAXDEPTH < j+1){
				MAXDEPTH = j+1;
			}
			// ここまで共通ルートなら
			if(isCommon){
				// 今回も共通か確認するためのフラグ
				var isCommonNow = false;
				// nodes配列に同じlinkedClassが既にあるか確認
				var targets = [];
				for(var k = 0; k < ret['nodes'].length; k++){
					// 同階層かつ同じ名前のものがあったらtargets配列に番号を追加
					if(ret['nodes'][k]['group'] == (j+1) && obj[i]['classLinks'][j]['linkedClass'] == ret['nodes'][k]['uri']){
						targets.push(k);
					}
				}

				// 既にあった場合はlinks配列に同じlinkが存在するか確認
				if(targets.length != 0){
					// 先ほど見つけたtargetsの数だけ繰り返しながら
					for(var l = 0; l <targets.length; l++){
						// links配列に全く同じ条件のものがあるか確認
						for(var m = 0; m < ret['links'].length; m++){
							// あった場合今回のものは追加せずsourceを更新して次へ
							if(ret['links'][m]['source'] == source && ret['links'][m]['target'] == targets[l] && ret['links'][m]['uri'] == obj[i]['classLinks'][j]['predicate'] && !isCommonNow){
								// 共通ルートフラグをオン
								isCommonNow = true;
								source = targets[l];
							}
						}
					}

					// 各targetを確認して共通ルートではなかった場合新規追加
					if(!isCommonNow){
						isCommon = false;
						ret['nodes'].push({'name': obj[i]['classLinks'][j]['label'], 'uri': obj[i]['classLinks'][j]['linkedClass'], 'group': (j+1), 'x':0, 'y':0, 'nodeid':ret['nodes'].length, 'path': 'notend', 'nodecolor': '#cccccc'});
						ret['links'].push({'source':source, 'target':ret['nodes'].length - 1, 'property': propertytext, 'uri': obj[i]['classLinks'][j]['predicate']});
						source = ret['nodes'].length - 1;
					}

				// なかった場合は別条件なので新規追加して次へ
				}else{

					isCommon = false;
					ret['nodes'].push({'name': obj[i]['classLinks'][j]['label'], 'uri': obj[i]['classLinks'][j]['linkedClass'], 'group': (j+1), 'x':0, 'y':0, 'nodeid':ret['nodes'].length, 'path': 'notend', 'nodecolor': '#cccccc'});
					ret['links'].push({'source':source, 'target':ret['nodes'].length - 1, 'property': propertytext, 'uri': obj[i]['classLinks'][j]['predicate']});
					source = ret['nodes'].length - 1;
				}
			// 既に共通ルートでないなら新規追加して次へ
			}else{
				ret['nodes'].push({'name': obj[i]['classLinks'][j]['label'], 'uri': obj[i]['classLinks'][j]['linkedClass'], 'group': (j+1), 'x':0, 'y':0, 'nodeid':ret['nodes'].length, 'path': 'notend', 'nodecolor': '#cccccc'});
				ret['links'].push({'source':source, 'target':ret['nodes'].length - 1, 'property': propertytext, 'uri': obj[i]['classLinks'][j]['predicate']});
				source = ret['nodes'].length - 1;
			}

		}
		// 最後（末端ノード）に各種値を追加
		ret['nodes'][ret['nodes'].length - 1]['path'] = obj[i];
		ret['nodes'][ret['nodes'].length - 1]['score'] = score;
		ret['nodes'][ret['nodes'].length - 1]['nodecolor'] = 'hsl(150, 50%, 75%)';
		// パス数を追加
		PATHNUM++;
	}
	// ノード間のスペースを計算
	TREESPACE = $('.SBGraph').width() / (MAXDEPTH + 1);

	// できた結果を返す
	return ret;
};

set_map_location = function(myNodeIndex, nodes, links, depth, fromAngle, toAngle){

    // depthが未定義ならば0をセット
    if (depth == undefined){
        depth = 0;
    }

    // 各種初期化
    var children = undefined;
    var parent = undefined;
    var parentsChildren = undefined;

    // links配列の数だけ繰り返しながら
    for (var i=0; i<links.length; i++){
        // そのlinksのtargetがmyNodeIndexならparentをセット
        if (links[i].target == myNodeIndex){
            parent = links[i].source;
        }
    }

    // parentが見つかっていたならば
    if (parent != undefined){
        // parentとlinksを渡しget_childrenメソッドを実行
        parentsChildren = get_children(parent, links);
    }

    if(myNodeIndex != 0){
        DRAWHEIGHT += (NODEHEIGHT * 1.5);
        var x = (depth * TREESPACE) + (TREESPACE / 3);
        var y = DRAWHEIGHT;
        nodes[myNodeIndex].x = x;
        nodes[myNodeIndex].y = y;
    }else{
        var x = TREESPACE / 3;
        var y = (NODEHEIGHT * 1.5) * ((PATHNUM - 1) / 2) + NODEHEIGHT;
        nodes[myNodeIndex].x = x;
        nodes[myNodeIndex].y = y;
    }

    children = get_children(myNodeIndex, links);

    for (var i=0; i<children.length; i++){
        if(i == 0){
            DRAWHEIGHT -= (NODEHEIGHT * 1.5);
        }
        var child = children[i];
        set_map_location(child, nodes, links, depth+1, fromAngle + ((toAngle - fromAngle) / children.length) * i, fromAngle + ((toAngle - fromAngle) / children.length) * (i+1));
    }

};

// 指定された親が持つ子を返す
get_children = function(index, links){
    var children = new Array();
    // linksの数だけ確認しながら
    for (var i=0; i<links.length; i++){
        // 親が渡された親と一致する時の子を追加
        if (links[i].source == index){
            children.push(links[i].target);
        }
    }
    return children;
};
