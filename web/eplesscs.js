
// API用プレフィックス指定
var prefix = 'http://www.sparqlbuilder.org';

// ページ読み込み完了次第
$(function(){

	// クラス選択用divのスクロール設定
	scrolldiv();

	// スタートクラスの読み込み
	loadStartClass();

	// スタートクラス・エンドクラスの検索用テキストボックスそれぞれについて
	// キーが押されたとき
	$('#searchstarttext, #searchendtext').keypress(function(e) {
		// エンターキーだったらfalseを返す（テキストボックスのデフォルト機能をキャンセル）
		if ( e.which == 13 ) {
			return false;
		}
	});
	// キーが押され、離された時（離された時をイベントハンドラにしないとその時入力した文字が反映されない）
	$('#searchstarttext, #searchendtext').keyup(function(e) {
		// 検索を行う
		search();
	});
});

// クラス選択用divのスクロール設定
function scrolldiv(){

	// スタートクラス一覧のテーブルとスタートクラスの検索用テキストボックス欄の高さの合計が指定済みの左カラムの高さより大きければ
	if(($('div.startclass table').height() + $('div.searchstart').height()) > $('div.left').height()){
		// スタートクラス一覧用divの縦スクロール設定をオンに
		$('div.startclass').css('overflow-y', 'scroll');
		// 高さを左カラムから検索用テキストボックス欄と20px（padding×4）引いた値にする
		$('div.startclass').css('height', ($('div.left').height() - $('div.searchstart').height() - 20) + 'px');
	// 小さければ
	}else{
		// 縦スクロールをオフ
		$('div.startclass').css('overflow-y', 'hidden');
	}
	// エンドクラスについて同様の処理
	if(($('div.endclass table').height() + $('div.searchend').height()) > $('div.right').height()){
		$('div.endclass').css('overflow-y', 'scroll');
		$('div.endclass').css('height', ($('div.right').height() - $('div.searchend').height() - 20) + 'px');
	}else{
		$('div.endclass').css('overflow-y', 'hidden');
	}
}

// スタートクラスの読み込み
function loadStartClass(){
	// SPARQL Builderのスタートクラス一覧取得用APIアドレスを作成
	var url = prefix + "/api/clist";
	// AJAX開始
	$.ajax({
		// メソッドタイプ：ゲット
		type : "GET",
		// URL：先ほどセットしたもの
		url : url,
		// 取得成功したら
		success : function(data) {
			// 帰ってきた結果をJSONにパースし取得
			var list = eval(data);
			// スタートクラスの一覧部分を空に
			$('div.startclass').empty();

			// スタートクラスのテーブルを作成
			var startclasstable = $('<table>');

            // 前のURIを空で作成
            var prevuri = "";

			// 結果のエンドポイントの分だけ繰り返し
			for(var i = 0; i < list.length; i++){
                // 今回のURIが前のURIと違ったら
                if(prevuri != list[i]['uri']){
                    // 保存しているURIを保存
                    prevuri = list[i]['uri'];
                    // 今回分の情報を入れ行を追加
				    startclasstable.append('<tr><td class="startclasscell"><span class="startclassuri" title="' + list[i]['uri'] + '">' + list[i]['label'] + '</span><span class="endpointuri" title="' + list[i]['ep'] + '"></span></td></tr>');
                // 同じURIなら
                }else{
                    // 現在最後のエンドポイントURI要素を探し後ろにエンドポイントを追記
                    startclasstable.find('.endpointuri').last().after($('<span class="endpointuri" title="' + list[i]['ep'] + '"></span>'));
                }
			}

			// スタートクラス一覧に追加
			$('div.startclass').append(startclasstable);

			// エンドクラスのリクエスト機能を有効化
			requestEndClass();
			// クラス選択用divのスクロール設定
			scrolldiv();
		}
	});
}

// エンドクラスのリクエスト機能を有効化
function requestEndClass(){
	// スタートクラス用の各URIについて
	$('.startclassuri').each(function(){
		// 現在のクリックイベントを削除（多重化対策）
		$(this).unbind('click');
		// クリックイベントの追加
		$(this).click(function(){

			// 選択済みスタートクラスのクラスがあれば除去
			$('.selectedstart').each(function(){
				$(this).removeClass('selectedstart');
			});
			// クリックされた要素に選択済みスタートクラスを追加
			$(this).addClass('selectedstart');

            // 選択済みエンドポイントのクラスがあれば除去
            $('.selectedendpoint').each(function(){
                $(this).removeClass('selectedendpoint');
            });

            // このURIが単一のエンドポイントにあれば
            if($(this).parent().children('.endpointuri').length == 1){
                // エンドクラス一覧部分を空に
                $('.endclass').empty();
                // AJAXローディング画像を追加
                $('.endclass').append('<img src="images/ajax-loader.gif">');

                // クリックされたクラスのエンドポイントを取得
                var ep = $(this).parent().children('.endpointuri').attr('title');
                // クリックされたクラスのURIを取得
                var sc = $(this).attr('title');
                // SPARQL Builderのエンドクラス一覧取得用APIアドレスを作成
                var url = prefix + "/api/clist?ep=" + encodeURIComponent(ep) + '&class=' + encodeURIComponent(sc);
                // AJAX開始
                $.ajax({
                    // メソッドタイプ：ゲット
                    type : "GET",
                    // URL：先ほどセットしたもの
                    url : url,
                    // 取得成功したら
                    success : function(data) {
                        // 帰ってきた結果をJSONにパースし取得
                        var list = eval(data);
                        // エンドクラスの一覧部分を空に
                        $('.endclass').empty();

                        // エンドクラスのテーブルを作成
                        var endclasstable = $('<table>');

                        // 取得したクラスリストの数だけ繰り返しながら
                        for (var i = 0; i < list.length; ++i) {
                            // 列を追加
                            endclasstable.append('<tr><td class="endclasscell"><span class="endpointuri" title="' + ep + '"></span><span class="startclassuri" title="' + sc + '"></span><span class="endclassuri" title="' + list[i]['uri'] + '">' + list[i]['label'] + ' (' + list[i]['number'] + ')' + '</span></td></tr>');
                        }

                        // エンドクラス一覧に追加
                        $('.endclass').append(endclasstable);

                        // SPARQL Builderのリクエスト機能を有効化
                        requestSPARQLBuilder();
                        // クラス選択用divのスクロール設定
                        scrolldiv();
                    }
                });
            // 複数エンドポイントにまたがっていれば
            }else{
                // エンドポイントURIを表示
                $(this).parent().children('.endpointuri').each(function(){
                    $(this).text($(this).attr('title'));
                });
                // クラス選択用divのスクロール設定
                scrolldiv();
            }
		});
	});

	// スタートクラス用URIの各エンドポイント選択について
	$('.endpointuri').each(function(){

		// 現在のクリックイベントを削除（多重化対策）
		$(this).unbind('click');
		// クリックイベントの追加
		$(this).click(function(){

			// 選択済みスタートクラスのクラスがあれば除去
			$('.selectedstart').each(function(){
				$(this).removeClass('selectedstart');
			});
			// クリックされた要素に選択済みスタートクラスを追加
			$(this).parent().children('.startclassuri').addClass('selectedstart');

            // 選択済みエンドポイントのクラスがあれば除去
            $('.selectedendpoint').each(function(){
                $(this).removeClass('selectedendpoint');
            });
            // クリックされた要素に選択済みエンドポイントクラスを追加
            $(this).addClass('selectedendpoint');

            // エンドクラス一覧部分を空に
            $('.endclass').empty();
            // AJAXローディング画像を追加
            $('.endclass').append('<img src="images/ajax-loader.gif">');

            // クリックされたクラスのエンドポイントを取得
            var ep = $(this).attr('title');
            // クリックされたクラスのURIを取得
            var sc = $(this).parent().children('.startclassuri').attr('title');
            // SPARQL Builderのエンドクラス一覧取得用APIアドレスを作成
            var url = prefix + "/api/clist?ep=" + encodeURIComponent(ep) + '&class=' + encodeURIComponent(sc);
            // AJAX開始
            $.ajax({
                // メソッドタイプ：ゲット
                type : "GET",
                // URL：先ほどセットしたもの
                url : url,
                // 取得成功したら
                success : function(data) {
                    // 帰ってきた結果をJSONにパースし取得
                    var list = eval(data);
                    // エンドクラスの一覧部分を空に
                    $('.endclass').empty();

                    // エンドクラスのテーブルを作成
                    var endclasstable = $('<table>');

                    // 取得したクラスリストの数だけ繰り返しながら
                    for (var i = 0; i < list.length; ++i) {
                        // 列を追加
                        endclasstable.append('<tr><td class="endclasscell"><span class="endpointuri" title="' + ep + '"></span><span class="startclassuri" title="' + sc + '"></span><span class="endclassuri" title="' + list[i]['uri'] + '">' + list[i]['label'] + ' (' + list[i]['number'] + ')' + '</span></td></tr>');
                    }

                    // エンドクラス一覧に追加
                    $('.endclass').append(endclasstable);

                    // SPARQL Builderのリクエスト機能を有効化
                    requestSPARQLBuilder();
                    // クラス選択用divのスクロール設定
                    scrolldiv();
                }
            });
		});
	});
}

// SPARQL Builderのリクエスト機能を有効化
function requestSPARQLBuilder(){

	// エンドクラスのURIそれぞれについて
	$('.endclassuri').each(function(){
		// クリックイベントの削除（多重化問題対策）
		$(this).unbind('click');
		// クリックイベントの追加
		$(this).click(function(){

			// 選択済みエンドクラスを除去
			$('.selectedend').each(function(){
				$(this).removeClass('selectedend');
			});
			// 今回の要素を選択済みエンドクラスにする
			$(this).addClass('selectedend');

			// エンドポイントURL・スタートクラスURI・エンドクラスURIを取得
			var ep = $(this).parent().children('.endpointuri').attr('title');
			var sc = $(this).parent().children('.startclassuri').attr('title');
			var ec = $(this).attr('title');

			// SPARQL Builderの呼び出し用URLを作成
			var url = prefix + "/?ep=" + encodeURIComponent(ep) + '&st=' + encodeURIComponent(sc) + '&en=' + encodeURIComponent(ec);

			// 上で作成したURLを新しいウィンドウで開く
			window.open(url);
		});
	})
}

// URIの検索
function search(){
	// スタートクラス用検索欄の値が空なら
	if($('#searchstarttext').val() == ''){
		// スタートクラスのノーヒットクラスを削除
		$('.startclass .nohit').removeClass('nohit');
	// 空でなければ
	}else{
		// 入力された値を小文字にして取得
		var keyword = $('#searchstarttext').val().toLowerCase();
		// スタートクラス一覧それぞれについて
		$('.startclasscell').each(function(){
			// スタートクラスの表示名部分を小文字にして取得
			var label = $(this).children('.startclassuri').text().toLowerCase();
			// 取得した文字列に検索ワードが含まれているかチェック（両方小文字に変換済みなので大文字小文字を問わない）
			if(label.indexOf(keyword) != -1){
				// 含まれていればノーヒットクラスを除去
				$(this).parent().removeClass('nohit');
			}else{
				// 含まれていなければノーヒットクラスを追加（非表示になる）
				$(this).parent().addClass('nohit');
			}
		});
	}
	// エンドクラスについて同様の処理
	if($('#searchendtext').val() == ''){
		$('.endclasscell .nohit').removeClass('nohit');
	}else{
		var keyword = $('#searchendtext').val().toLowerCase();
		$('.endclasscell').each(function(){
			var label = $(this).children('.endclassuri').text().toLowerCase();
			if(label.indexOf(keyword) != -1){
				$(this).parent().removeClass('nohit');
			}else{
				$(this).parent().addClass('nohit');
			}
		});
	}

	// 30ms待ってからスクロール再セット（描画変更にかかる時間を考慮）
	setTimeout('scrolldiv()',30);
}