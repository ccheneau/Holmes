package net.holmes.core;

import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.configuration.XmlConfigurationImpl;
import net.holmes.core.model.IContentTypeFactory;
import net.holmes.core.model.ContentTypeFactoryImpl;
import net.holmes.core.service.IMediaService;
import net.holmes.core.service.MediaServiceImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class TestModule extends AbstractModule
{

    /* (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure()
    {
        bind(IConfiguration.class).to(XmlConfigurationImpl.class).in(Singleton.class);

        bind(IMediaService.class).to(MediaServiceImpl.class).in(Singleton.class);

        bind(IContentTypeFactory.class).to(ContentTypeFactoryImpl.class).in(Singleton.class);

    }

}
