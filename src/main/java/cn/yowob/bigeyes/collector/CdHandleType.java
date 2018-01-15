package cn.yowob.bigeyes.collector;

/**
 * @author: 陈刚 2016/4/6
 */
public class CdHandleType {
	/**
	 * 固定的值
	 */
	public static final int FIXED_VALUE = 0;
	/**
	 * 标准的表格,前一个td是‘名称’,跟着后一个td就是‘值’
	 */
	public static final int TABLE_KEY_VALUE = 1;

	/**
	 * 标准的表格列表, 和thead和tbody
	 */
	public static final int TABLE_STANDARD = 2;
	/**
	 * 变形的表格列表, 没有thead, 表头就是tbody里, 而且是第一个tr. 列表标题在第二个tr
	 */
	public static final int TABLE2 = 3;
	/**
	 * 信息分散在多个表中
	 */
	public static final int TABLE_MULTI = 4;
}
