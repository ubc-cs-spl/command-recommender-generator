package ca.ubc.cs.commandrecommender.commandinfogen.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;
import org.eclipse.jface.dialogs.MessageDialog;

import ca.ubc.cs.commandrecommender.commandinfogen.utils.CommandInfoStorage;
import ca.ubc.cs.commandrecommender.commandinfogen.utils.CommandInfoUtils;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class InfoGenHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public InfoGenHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IViewRegistry viewRegistry = PlatformUI.getWorkbench().getViewRegistry();
		IViewDescriptor[] views = viewRegistry.getViews();
		//Go through all views
		for (IViewDescriptor iViewDescriptor : views) {
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(iViewDescriptor.getId());
				CommandInfoStorage.storeAllKnownCommandInfo();
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		/* 
		//Go through all perspectives  (This does not seem to be useful, but might be useful later.
		IPerspectiveRegistry perspectiveRegistry = PlatformUI.getWorkbench().getPerspectiveRegistry();
        IPerspectiveDescriptor[] perspectives = perspectiveRegistry.getPerspectives();
        for (IPerspectiveDescriptor perspective : perspectives) {
            try {
                PlatformUI.getWorkbench().showPerspective(perspective.getId(), 
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow());

            } catch (WorkbenchException e) {
                e.printStackTrace();
            }
        }
		 */
		MessageDialog.openInformation(
				window.getShell(),
				"Commandinfogen",
				"Command Info Stored into Database");
		return null;
	}
}
