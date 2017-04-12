package com.dilapp.radar.util;

import java.util.Collection;

public class CollectionUtil {

	/**
	 * 
	 * 判断集合是否为空
	 */

	public static boolean isEmpty(Collection<? extends Object> collection) {
		boolean isEmpty = true;
		if (collection != null && !collection.isEmpty()) {
			isEmpty = false;
		}
		return isEmpty;
	}

}
