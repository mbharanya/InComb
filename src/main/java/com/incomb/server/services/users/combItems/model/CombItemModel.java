package com.incomb.server.services.users.combItems.model;

import java.sql.Timestamp;

import com.incomb.server.model.CombItem;
/**
 * Simple Bean that can change the ReadDate of a CombItem
 * Used for communication with the client
 */
public class CombItemModel extends CombItem {

	private static final long serialVersionUID = 8877227578765402343L;

	/**
	 * Default constructor calls {@link CombItem}s Constructor
	 */
	public CombItemModel() {
		super();
	}

	
	/**
	 * Sets safe values of a {@link CombItem} in a {@link CombItemModel}
	 * these are the userId, contentId, addDate, readDate
	 * @param combItem to get the values from
	 */
	public CombItemModel(final CombItem combItem) {
		super(combItem.getUserId(), combItem.getContentId(), combItem.getAddDate(), combItem.getReadDate());
	}

	/**
	 * Sets the read date of the {@link CombItemModel}
	 * @param readDate to set (time milliseconds since January 1, 1970, 00:00:00 GMT. A negative number is the number of milliseconds before January 1, 1970, 00:00:00 GMT.)
	 */
	public void setReadDate(final long readDate) {
		super.setReadDate(new Timestamp(readDate));
	}
}
