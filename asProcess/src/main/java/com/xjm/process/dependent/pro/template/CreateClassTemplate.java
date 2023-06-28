package com.xjm.process.dependent.pro.template;

import freemarker.cache.StrongCacheStorage;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.SimpleMapModel;
import freemarker.template.*;
import com.xjm.process.dependent.pro.build.FreeWork;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * title: CreateClassTemplate
 * description:
 *
 * @author xujianmin
 * @version 1.0.0
 * time 2023/4/20 15:26
 **/
public class CreateClassTemplate {

    private static final Configuration CONFIGURATION;


    static {
        CONFIGURATION = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        CONFIGURATION.setTemplateLoader(new SimpleClasspathLoader());
        // do not refresh/gc the cached templates, as we never change them at runtime
        CONFIGURATION.setCacheStorage(new StrongCacheStorage());
        CONFIGURATION.setTemplateUpdateDelayMilliseconds(Integer.MAX_VALUE);
        CONFIGURATION.setLocalizedLookup(false);
        CONFIGURATION.setObjectWrapper(new DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));

    }

    private static final class SimpleClasspathLoader implements TemplateLoader {
        @Override
        public Reader getReader(Object name, String encoding) throws IOException {
            URL url = getClass().getClassLoader().getResource(String.valueOf(name));
            if (url == null) {
                throw new IllegalStateException(name + " not found on classpath");
            }
            URLConnection connection = url.openConnection();

            // don't use jar-file caching, as it caused occasionally closed input streams [at least under JDK 1.8.0_25]
            connection.setUseCaches(false);

            InputStream is = connection.getInputStream();

            return new InputStreamReader(is, StandardCharsets.UTF_8);
        }

        @Override
        public long getLastModified(Object templateSource) {
            return 0;
        }

        @Override
        public Object findTemplateSource(String name) throws IOException {
            return name;
        }

        @Override
        public void closeTemplateSource(Object templateSource) throws IOException {
        }
    }


    /**
     * description
     *
     * @param mFiler      写入
     * @param packageName 包名
     *                    version 1.0.0
     *                    time 2023/4/20 15:31
     */
    public static void createCLass(Filer mFiler, String packageName, FreeWork freeWork) {
        Template template = null;

        try {
            template = CONFIGURATION.getTemplate("/process/GeneratedType.ftl");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringWriter templateResults = new StringWriter();


        try {
            template.process(
                    new ExternalParamsTemplateModel(
                            new BeanModel(freeWork, BeansWrapper.getDefaultInstance()),
                            null
                    ), templateResults);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        String s = templateResults.toString();

        try {
            JavaFileObject source = mFiler.createSourceFile(packageName + "." + freeWork.getName());
            Writer writer = source.openWriter();
            writer.write(s);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    private static class ExternalParamsTemplateModel implements TemplateHashModel {

        private final BeanModel object;
        private final SimpleMapModel extParams;

        ExternalParamsTemplateModel(BeanModel object, SimpleMapModel extParams) {
            this.object = object;
            this.extParams = extParams;
        }

        @Override
        public TemplateModel get(String key) throws TemplateModelException {
            if (key.equals("ext")) {
                return extParams;
            } else {
                return object.get(key);
            }
        }

        @Override
        public boolean isEmpty() throws TemplateModelException {
            return object.isEmpty() && extParams.isEmpty();
        }
    }


}
