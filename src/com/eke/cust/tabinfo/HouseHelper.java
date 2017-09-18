package com.eke.cust.tabinfo;

import com.eke.cust.model.HouseSource;

import java.util.ArrayList;

public class HouseHelper {
	// 数据重组
	public static ArrayList<HouseSource> buildList(
			ArrayList<HouseSource> houses) {
		ArrayList<HouseSource> rentSellHouses = new ArrayList<HouseSource>();
		int Sell = 0;
		int Rent = 5;
		for (int i = 0; i < houses.size(); i++) {
			rentSellHouses.add(houses.get(i));

		}
		for (int i = 0; i < houses.size(); i++) {
			if (i % 2 == 0) {
				rentSellHouses.set(i, houses.get(Sell));
				Sell++;
			} else {
				rentSellHouses.set(i, houses.get(Rent));
				Rent++;

			}

		}

		return rentSellHouses;

	}
}
