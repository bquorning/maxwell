package com.zendesk.maxwell.schema;

import com.zendesk.maxwell.MaxwellContext;
import com.zendesk.maxwell.MaxwellTestSupport;
import com.zendesk.maxwell.MaxwellTestWithIsolatedServer;
import com.zendesk.maxwell.replication.BinlogPosition;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class PositionStoreThreadTest extends MaxwellTestWithIsolatedServer {
	private MysqlPositionStore buildStore(MaxwellContext context) throws Exception {
		return new MysqlPositionStore(context.getMaxwellConnectionPool(), context.getServerID(), "maxwell", MaxwellTestSupport.inGtidMode());
	}

	@Test
	public void testStoresFinalPosition() throws Exception {
		MaxwellContext context = buildContext();
		MysqlPositionStore store = buildStore(context);
		BinlogPosition initialPosition = new BinlogPosition(4L, "file");
		BinlogPosition finalPosition = new BinlogPosition(88L, "file");
		PositionStoreThread thread = new PositionStoreThread(store, context);

		thread.setPosition(initialPosition);
		thread.setPosition(finalPosition);
		thread.storeFinalPosition();

		assertThat(store.get(), is(finalPosition));
	}

	@Test
	public void testDoesNotStoreUnchangedPosition() throws Exception {
		MaxwellContext context = buildContext();
		MysqlPositionStore store = buildStore(context);
		BinlogPosition initialPosition = new BinlogPosition(4L, "file");
		PositionStoreThread thread = new PositionStoreThread(store, context);

		thread.setPosition(initialPosition);
		thread.storeFinalPosition();

		assertThat(store.get(), nullValue());
	}
}
