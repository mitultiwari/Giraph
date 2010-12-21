package com.yahoo.hadoop_bsp;

import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.ipc.VersionedProtocol;
/*
import org.apache.hadoop.security.token.TokenInfo;
*/

/**
 * Basic interface for communication between workers.
 * 
 *
 * @param <I extends Writable> vertex id
 * @param <M extends Writable> message data
 *
 **/
/*
@TokenInfo(BspTokenSelector.class)
*/
public interface CommunicationsInterface
      <I extends Writable, M extends Writable>
      extends VersionedProtocol {

	/**
	 * Interface Version History
	 * 
	 * 0 - First Version
	 */
	static final long versionID = 0L;
	
  /**
   * Adds incoming message.
   * 
   * @param vertex
   * @param msg
   * @throws IOException
   */
  void put(I vertex, M msg)
      throws IOException;
  
  /**
   * Adds incoming message list.
   * 
   * @param vertex
   * @param msg
   * @throws IOException
   */
  void put(I vertex, MsgArrayList<M> msgList)
      throws IOException;
  
  /**
   * @return The name of this worker in the format "hostname:port".
   */
  String getName();

}
