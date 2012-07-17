package net.holmes.core.http;

import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;

public interface IChannelPipelineFactory extends ChannelPipelineFactory {

    public void setChannelGroup(ChannelGroup channelGroup);

}
