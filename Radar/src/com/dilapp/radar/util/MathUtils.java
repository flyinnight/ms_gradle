package com.dilapp.radar.util;

import android.graphics.Point;

/**
 * ���֡���ѧ��ز���������
 * 
 * @Create Date: 2012-10-16
 */
public class MathUtils {

	/**
	 * ŷ�����շת����㷨,��a��b���������С������
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private static int basal(int a, int b) {
		int i = a, j = b, s;
		for (; j != 0 && (s = i % j) != 0; i = j, j = s)
			; // ����a��b�����Լ��j (ŷ�����շת����㷨)
		return (j == 0 ? 0 : (a * b / j)); // �õ���С������ (=���ǵĳ˻�������Լ��)
	}

	/**
	 * �����㷨,����������p������Ԫ�ص���С������
	 * 
	 * @param p
	 * @return
	 */
	public static int commonMultiple(int p[]) {
		for (int i = 0; i < p.length; i++) {
			if (p[i] == 0) {
				p[i] = 1;
			}
		}
		if (p.length <= 2) {
			return (p.length == 1 ? p[0] : basal(p[0], p[1])); // ��
		}
		int[] t1 = new int[p.length / 2];
		int[] t2 = new int[p.length - t1.length];
		System.arraycopy(p, 0, t1, 0, t1.length); // ���ֲ�1
		System.arraycopy(p, t1.length, t2, 0, t2.length); // ���ֲ�2
		p = null;
		int result1 = commonMultiple(t1); // ���?1
		int result2 = commonMultiple(t2); // ���?2
		return basal(result1, result2); // ��ϲ�
	}

	/**
	 * ����Сֵ���ֵ
	 * 
	 * @param values
	 * @return result[0]:��Сֵ result[1]:���ֵ
	 */
	public static int[] minMax(int[] values) {
		// min:result[0]
		// max:result[1]
		int[] result = new int[] { 0, 0 };
		if (values == null) {
			return result;
		}
		int length = values.length;
		if (length == 0) {
			return result;
		}
		if (length == 1) {
			result[0] = values[0];
			result[1] = values[0];
			return result;
		}
		if (length == 2) {
			result[0] = Math.min(values[0], values[1]);
			result[1] = Math.max(values[0], values[1]);
			return result;
		}

		result[0] = Math.min(values[0], values[length - 1]);
		result[1] = Math.max(values[0], values[length - 1]);

		int min = result[0];
		int max = result[1];
		int count = length / 2;
		for (int i = 1; i <= count; i++) {
			int front = values[i];
			int back = values[length - i - 1];
			if (front > back) {
				if (front > max) {
					max = front;
				}
				if (back < min) {
					min = back;
				}
			} else {
				if (front < min) {
					min = front;
				}
				if (back > max) {
					max = back;
				}
			}
		}
		// ---------------------------------------------
		return result;
	}

	/**
	 * ����Сֵ���ֵ
	 * 
	 * @param values
	 * @return result[0]:��Сֵ result[1]:���ֵ
	 */
	public static double[] minMax(double[] values) {
		// min:result[0]
		// max:result[1]
		double[] result = new double[] { 0, 0 };
		if (values == null) {
			return result;
		}
		int length = values.length;
		if (length == 0) {
			return result;
		}
		if (length == 1) {
			result[0] = values[0];
			result[1] = values[0];
			return result;
		}
		if (length == 2) {
			result[0] = Math.min(values[0], values[1]);
			result[1] = Math.max(values[0], values[1]);
			return result;
		}

		result[0] = Math.min(values[0], values[length - 1]);
		result[1] = Math.max(values[0], values[length - 1]);

		double min = result[0];
		double max = result[1];
		int count = length / 2;
		for (int i = 1; i <= count; i++) {
			double front = values[i];
			double back = values[length - i - 1];
			if (front > back) {
				if (front > max) {
					max = front;
				}
				if (back < min) {
					min = back;
				}
			} else {
				if (front < min) {
					min = front;
				}
				if (back > max) {
					max = back;
				}
			}
		}
		// ---------------------------------------------
		return result;
	}

	/**
	 * �����ľ���
	 * 
	 * @param point1
	 * @param point2
	 * @return
	 */
	public static double pointDistance(Point point1, Point point2) {
		if (point1 == null || point2 == null) {
			return -1;
		}
		return Math.sqrt((point1.x - point2.x) * (point1.x - point2.x)
				+ (point1.y - point2.y) * (point1.y - point2.y));
	}

	/**
	 * ��������
	 * 
	 * @param num
	 *            ��Ҫ���С��
	 * @param n
	 *            ������λ
	 * @return ���
	 */
	public static double round(float num, int n) {
		// float ת double �ᶪʧ���ȣ�������Ҫ���⴦��
		return round(Double.parseDouble(num + ""), n);
	}

	/**
	 * ��������
	 * 
	 * @param num
	 *            ��Ҫ���С��
	 * @param n
	 *            ������λ
	 * @return ���
	 */
	public static double round(double num, int n) {
		return Double.parseDouble(String.format("%." + n + "f", num));
	}
}
