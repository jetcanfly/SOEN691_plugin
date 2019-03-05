package tutorial691.handlers;

import java.util.HashSet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import tutorial691.patterns.ExceptionFinder;


public class DetectException extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		IProject[] projects = root.getProjects();

		detectInProjects(projects);

		SampleHandler.printMessage("DONE DETECTING");
		System.out.println("DONE DETECTING");

		return null;
	}

	private void detectInProjects(IProject[] projects) {
		for(IProject project : projects) {
			System.out.println("DETECTING IN: " + project.getName());
//			findSubTypes(project, "tryCatchPolymorphism.SuperInterface");
			ExceptionFinder exceptionFinder = new ExceptionFinder();
			try {
				exceptionFinder.findExceptions(project);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}	
		}
	}
	
	/**
	 * @param project
	 * @param superName
	 * @return
	 * 
	 * There is a serious bug in method: iType.newTypeHierarchy(jProject, new NullProgressMonitor())
	 * and getAllTypes() on this Hierarchy. It always returns incomplete subTypes.
	 * So I traverse every Package that is actually in target workspace to get a complete set of subTypes.
	 */
	static public HashSet<IType> findSubTypes(IProject project, String superName) {
		IJavaProject jProject = JavaCore.create(project);
		HashSet<IType> iTypesSet = new HashSet<IType>();
		try {
			IType iType = jProject.findType(superName);
			IPackageFragment[] packages = jProject.getPackageFragments();
			for(IPackageFragment mypackage : packages){
				if(mypackage.getCompilationUnits().length != 0) {
					ITypeHierarchy ih = iType.newTypeHierarchy(mypackage.getCompilationUnits(), new NullProgressMonitor());
					if(ih != null) {
						IType[] iTypes = ih.getAllSubtypes(iType);
						for(IType t: iTypes) {
							iTypesSet.add(t);
						}
					}
				}
			}
			
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}
		return iTypesSet;
	}
}


