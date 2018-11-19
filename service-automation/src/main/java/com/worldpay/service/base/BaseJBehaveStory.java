package com.worldpay.service.base;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbehave.core.Embeddable;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.EmbedderControls;
import org.jbehave.core.i18n.LocalizedKeywords;
import org.jbehave.core.io.CasePreservingResolver;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryLoader;
import org.jbehave.core.io.StoryPathResolver;
import org.jbehave.core.junit.JUnitStory;
import org.jbehave.core.model.ExamplesTableFactory;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.ParameterControls;
import org.jbehave.core.steps.ParameterConverters;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import com.worldpay.service.entities.SharedData;
import com.worldpay.service.util.EnvironmentUtil;
import com.worldpay.service.util.FileUtil;

import de.codecentric.jbehave.junit.monitoring.JUnitReportingRunner;
import io.restassured.internal.RestAssuredResponseImpl;

@RunWith(JUnitReportingRunner.class)
public class BaseJBehaveStory extends JUnitStory {

    protected Object[] steps;
    private final CrossReference xref = new CrossReference();

    private org.slf4j.Logger logger = LoggerFactory.getLogger(BaseJBehaveStory.class);
    protected SharedData share = new SharedData();

    protected static final String STORY_TIMEOUT = "storyTimeout";
    protected static final int NUMBER_OF_THREADS = 1;
    protected static final String META_FILTER_SYSTEM_PROPERTY = "meta.filter";

    public BaseJBehaveStory(Object... steps) {
        addSteps(steps);
    }

    public void addSteps(Object... steps) {
        this.steps = steps;
        Embedder configuredEmbedder = configuredEmbedder();
        EmbedderControls embedderControls = configuredEmbedder.embedderControls();

        embedderControls.useThreads(1).useStoryTimeouts(FileUtil.readProp(EnvironmentUtil.GENERAL_PROPERTIES_PATH, STORY_TIMEOUT)).useThreads(NUMBER_OF_THREADS)
                .doIgnoreFailureInStories(true).ignoreFailureInView();
        JUnitReportingRunner.recommendedControls(configuredEmbedder);
        configuredEmbedder.useMetaFilters(getMetaFilters());
    }

    public Object[] getSteps() {
        return steps;
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        return new InstanceStepsFactory(configuration(), steps);
    }

    @Override
    public Configuration configuration() {

        Class<? extends Embeddable> embeddableClass = this.getClass();
        ParameterConverters parameterConverters = new ParameterConverters();

        ExamplesTableFactory examplesTableFactory = new ExamplesTableFactory(new LocalizedKeywords(), new LoadFromClasspath(embeddableClass),
                parameterConverters);
        parameterConverters.addConverters(new ParameterConverters.DateConverter(new SimpleDateFormat("yyyy-MM-dd")),
                new ParameterConverters.ExamplesTableConverter(examplesTableFactory));

        share.setResponse(new RestAssuredResponseImpl());
        share.setTestData(new CompositeConfiguration());
        share.getTestData().setDelimiterParsingDisabled(true);

        return new MostUsefulConfiguration().useStoryLoader(new LoadFromClasspath(this.getClass())).useStoryPathResolver(storyPathResolver())
                .useStoryLoader(storyLoader())
                .useStoryReporterBuilder(new StoryReporterBuilder().withDefaultFormats().withFormats(Format.TXT, Format.CONSOLE, Format.HTML_TEMPLATE,
                        Format.HTML, Format.XML, Format.XML_TEMPLATE))
                .useStoryPathResolver(storyPathResolver()).useStoryLoader(storyLoader()).useParameterControls(parameterControls());
    }

    private StoryPathResolver storyPathResolver() {
        return new CasePreservingResolver();
    }

    private StoryLoader storyLoader() {
        return new LoadFromClasspath();
    }

    private ParameterControls parameterControls() {
        return new ParameterControls().useDelimiterNamedParameters(true);
    }
    
    /**
     * Override to specify only a subset of stories/scenarios to run based on jbehave meta info.
     * 
     * @see http://jbehave.org/reference/stable/meta-info.html
     * @see http://jbehave.org/reference/stable/meta-filtering.html#highlighter_883587
     */
    protected List<String> getMetaFilters() {
        String metaFilterProperty = System.getProperty(META_FILTER_SYSTEM_PROPERTY);
        List<String> properties = new LinkedList<String>();

        if (metaFilterProperty != null) {
            String[] metaFilterProperties = metaFilterProperty.split(",");
            properties = new LinkedList<String>(Arrays.asList(metaFilterProperties));
        }

        properties.add("-skip");

        return properties;
    }
}
