package tests.story;
import io.tapack.allure.jbehave.AllureReporter;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.Steps;
import steps.AddToCartSteps;

import java.util.ArrayList;
import java.util.List;

public class AddToCartStory extends BaseStory {

    public AddToCartStory(){
        useStepsFactory(stepsFactory());
    }

    @Override
    public Configuration configuration() {
        if (super.hasConfiguration()) {
            return super.configuration();
        }
        return new MostUsefulConfiguration()
                .useStoryReporterBuilder(new StoryReporterBuilder()
                        .withDefaultFormats().withFormats(Format.CONSOLE)
//                        .withReporters(new AllureReporter())
                );
    }

    @Override
    public InjectableStepsFactory stepsFactory(){
        ArrayList<Steps> stepFileList = new ArrayList<>();
        stepFileList.add(new AddToCartSteps(sharedData));
        return new InstanceStepsFactory(configuration(), stepFileList);
    }

    public List<String> storyPaths(){
        return new StoryFinder().findPaths(CodeLocations.codeLocationFromClass(this.getClass()),
                "**/stories/AddToCartStory.story","");
    }



}



