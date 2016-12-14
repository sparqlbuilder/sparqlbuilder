package org.sparqlbuilder.core;

/**
 * 1ステップリンク（述語）の向きを記述する
 * <p>
 * 向きの解釈はこの　Direction が使用される状況に応じて定まる
 * 
 * @author Norio KOBAYASHI
 * @since 28.01.2014
 * @version 29.01.2014
 */
public enum Direction {
	/**
	 *  順方向
	 */
	forward, 

	/**
	 *  逆方向
	 */
	reverse, 

	/**
	 *  両方向
	 */
	both;
}
