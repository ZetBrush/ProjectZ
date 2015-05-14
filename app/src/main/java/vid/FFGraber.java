package vid;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Arman on 5/14/15.
 */
public class FFGraber extends AsyncTask<String, Integer, Integer> implements ICommandProvider {
    Context ctx;

    public FFGraber(Context ctx){
        this.ctx=ctx;
    }

    @Override
    protected Integer doInBackground(String... pats) {




        return null;
    }

    @Override
    public String getCommand(String... param) {



        return null;
    }
}
