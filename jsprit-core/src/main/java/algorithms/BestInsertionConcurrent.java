/*******************************************************************************
 * Copyright (C) 2013  Stefan Schroeder
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package algorithms;


//
//
///**
// * 
// * @author stefan schroeder
// * 
// */
//
//final class BestInsertionConcurrent implements InsertionStrategy{
//	
//	public static BestInsertionConcurrent newInstance(RouteAlgorithm routeAlgorithm, ExecutorService executor, int nuOfThreads){
//		return new BestInsertionConcurrent(routeAlgorithm, executor, nuOfThreads);
//	}
//	
//	static class Batch {
//		List<VehicleRoute> routes = new ArrayList<VehicleRoute>();
//		
//	}
//	
//	private static Logger logger = Logger.getLogger(BestInsertionConcurrent.class);
//
//	private Random random = RandomNumberGeneration.getRandom();
//	
//	private RouteAlgorithm routeAlgorithm;
//	
////	private ExecutorService executor;
//	
//	private int nuOfBatches;
//
//	private ExecutorCompletionService<Insertion> completionService;
//
//	public void setRandom(Random random) {
//		this.random = random;
//	}
//	
//	private BestInsertionConcurrent(RouteAlgorithm routeAlgorithm, ExecutorService executor, int nuOfThreads) {
//		super();
//		this.routeAlgorithm = routeAlgorithm;
////		this.executor = executor;
//		logger.info("initialise " + this);
//		this.nuOfBatches = nuOfThreads;
//		completionService = new ExecutorCompletionService<Insertion>(executor);
//	}
//
//	@Override
//	public String toString() {
//		return "[name=concurrentBestInsertion]";
//	}
//
//	@Override
//	public void insertJobs(Collection<VehicleRoute> vehicleRoutes, Collection<Job> unassignedJobs) {
//		List<Job> unassignedJobList = new ArrayList<Job>(unassignedJobs);
//		Collections.shuffle(unassignedJobList, random);
////		informInsertionStarts(vehicleRoutes,unassignedJobs.size());
//		int inserted = 0;
//		for(final Job unassignedJob : unassignedJobList){
//			VehicleRoute insertIn = null;
//			Insertion bestInsertion = null;
//			double bestInsertionCost = Double.MAX_VALUE;
//			
//			List<Batch> batches = distributeRoutes(vehicleRoutes,nuOfBatches);
//			
//			for(final Batch batch : batches){
//				completionService.submit(new Callable<Insertion>() {
//					
//					@Override
//					public Insertion call() throws Exception {
//						return getBestInsertion(batch,unassignedJob);
//					}
//					
//				});
//				
//			}
//			
//			try{
//				for(int i=0;i<batches.size();i++){
//					Future<Insertion> futureIData = completionService.take();
//					Insertion insertion = futureIData.get();
//					if(insertion == null) continue;
//					if(insertion.getInsertionData().getInsertionCost() < bestInsertionCost){
//						bestInsertion = insertion;
//						bestInsertionCost = insertion.getInsertionData().getInsertionCost();
//					}
//				}
//			}
//			catch(InterruptedException e){
//				Thread.currentThread().interrupt();
//			} 
//			catch (ExecutionException e) {
//				e.printStackTrace();
//				logger.error(e.getCause().toString());
//				System.exit(1);
//			}	
//			
//			if(bestInsertion != null){
////				informBeforeJobInsertion(unassignedJob, bestInsertion.getInsertionData(), bestInsertion.getRoute());
//				insertIn = bestInsertion.getRoute();
////				logger.debug("insert job="+unassignedJob+" at index=" + bestInsertion.getInsertionData().getInsertionIndex() + " delta cost=" + bestInsertion.getInsertionData().getInsertionCost());
//				routeAlgorithm.insertJob(unassignedJob, bestInsertion.getInsertionData(), bestInsertion.getRoute());
//			} 
//			else {
////				VehicleRoute newRoute = VehicleRoute.emptyRoute();
////				InsertionData bestI = routeAlgorithm.calculateBestInsertion(newRoute, unassignedJob, Double.MAX_VALUE);
////				if(bestI instanceof InsertionData.NoInsertionFound) 
//				throw new IllegalStateException("given the vehicles, could not create a valid solution.\n\tthe reason might be" +
//							" inappropriate vehicle capacity.\n\tthe job that does not fit in any vehicle anymore is \n\t" + unassignedJob);
////				insertIn = newRoute;
////				informBeforeJobInsertion(unassignedJob,bestI,newRoute);
////				routeAlgorithm.insertJob(unassignedJob,bestI,newRoute);
////				vehicleRoutes.add(newRoute);
//			}
//			inserted++;
////			informJobInserted((unassignedJobList.size()-inserted), unassignedJob, insertIn);
//		}
////		informInsertionEndsListeners(vehicleRoutes);
//	}
//	
//	private Insertion getBestInsertion(Batch batch, Job unassignedJob) {
//		Insertion bestInsertion = null;
//		double bestInsertionCost = Double.MAX_VALUE;
//		for(VehicleRoute vehicleRoute : batch.routes){
//			InsertionData iData = routeAlgorithm.calculateBestInsertion(vehicleRoute, unassignedJob, bestInsertionCost);
//			if(iData instanceof NoInsertionFound) continue;
//			if(iData.getInsertionCost() < bestInsertionCost){
//				bestInsertion = new Insertion(vehicleRoute,iData);
//				bestInsertionCost = iData.getInsertionCost();
//			}
//		}
//		return bestInsertion;
//	}
//
//	private List<Batch> distributeRoutes(Collection<VehicleRoute> vehicleRoutes, int nuOfBatches) {
//		List<Batch> batches = new ArrayList<Batch>();
//		for(int i=0;i<nuOfBatches;i++) batches.add(new Batch()); 
//		/*
//		 * if route.size < nuOfBatches add as much routes as empty batches are available
//		 * else add one empty route anyway
//		 */
////		if(vehicleRoutes.size()<nuOfBatches){
////			int nOfNewRoutes = nuOfBatches-vehicleRoutes.size();
////			for(int i=0;i<nOfNewRoutes;i++){
////				vehicleRoutes.add(VehicleRoute.emptyRoute());
////			}
////		}
////		else{
//			vehicleRoutes.add(VehicleRoute.emptyRoute());
////		}
//		/*
//		 * distribute routes to batches equally
//		 */
//		int count = 0;
//		for(VehicleRoute route : vehicleRoutes){
//			if(count == nuOfBatches) count=0;
//			batches.get(count).routes.add(route);
//			count++;
//		}
//		return batches;
//	}
//
//
//	@Override
//	public void removeListener(InsertionListener insertionListener) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public Collection<InsertionListener> getListeners() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void addListener(InsertionListener insertionListener) {
//		// TODO Auto-generated method stub
//		
//	}
//
//}
