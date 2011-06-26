/**
 * This file is part of the Nilestore project.
 * 
 * Copyright (C) (2011) Nile University (NU)
 *
 * Nilestore is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eg.nileu.cis.nilestore.simulator;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class ComponentAppender.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class ComponentAppender extends AppenderSkeleton {

	/** The cmp. */
	private NsSimulator cmp;

	/**
	 * Instantiates a new component appender.
	 * 
	 * @param cmp
	 *            the cmp
	 */
	public ComponentAppender(NsSimulator cmp) {
		this.cmp = cmp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent
	 * )
	 */
	@Override
	protected void append(LoggingEvent event) {
		// TODO Auto-generated method stub
		String name = event.getLoggerName();
		if (name.startsWith("node") || name.startsWith("introducer")
				|| name.startsWith("monitor")) {
			String key = name.split("\\.")[0];

			cmp.addLoggingEvent(key, this.layout.format(event));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.AppenderSkeleton#close()
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.AppenderSkeleton#requiresLayout()
	 */
	@Override
	public boolean requiresLayout() {
		return true;
	}

}
