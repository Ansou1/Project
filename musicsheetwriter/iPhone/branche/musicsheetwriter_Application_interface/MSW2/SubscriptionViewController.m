//
//  SubscriptionViewController.m
//  MSW2
//
//  Created by simon on 11/10/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "SubscriptionViewController.h"
#import "ProfilViewController.h"
#import "GuestMode.h"

@interface SubscriptionViewController ()

@end

@implementation SubscriptionViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
    
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
    
    //UIEdgeInsets adjustForTabbarInsets = UIEdgeInsetsMake(60, 0, CGRectGetHeight(self.tabBarController.tabBar.frame), 0);
    //self.scrollView.contentInset = adjustForTabbarInsets;
    //self.scrollView.scrollIndicatorInsets = adjustForTabbarInsets;
    //NSLog(@"test ::: %@", Id_global);
    self.tableView.allowsMultipleSelectionDuringEditing = NO;
    
    //check if it is a guest mode
    GuestMode *guest = [[GuestMode alloc] init];
    if ([guest CheckIfTheUserIsAGuest] == true) {
        //NSLog(@"test test test");
        return;
    }

    [self refreshView];
}

-(void) refreshView{
    NSString * post =[NSString stringWithFormat:@"http://163.5.84.253/api/users/%@/subscriptions", Id_global];
    ApiMethod *api = [[ApiMethod alloc]init];
    NSDictionary *dict1 = [api getMethodWithString:post];
    //NSLog(@"test ::: %d", code_global);

    if (code_global != 200)
    {
        [api popup:dict1];
        return;
    }
    _pseudo = [[NSMutableArray alloc] initWithCapacity:0];
    _idRow = [[NSMutableArray alloc] initWithCapacity:0];
    //NSLog(@"%@", dict1);
    for(NSDictionary *item in dict1) {
        [_pseudo addObject:[item valueForKey:@"username"]];
        [_idRow addObject:[item valueForKey:@"id"]];
    }
}

// this two methods are here to swipe a cell in tableview
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return YES if you want the specified item to be editable.
    return YES;
}

// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        //add code here for when you hit delete
      
        int row = [indexPath row];
        
        NSString * post =[NSString stringWithFormat:@"http://163.5.84.253/api/users/%@/subscriptions/%@",Id_global, _idRow[row]];
        ApiMethod *api = [[ApiMethod alloc]init];
        NSDictionary *dict1 = [api deleteMethodWithString:post];
        if (code_global != 204)
        {
            [api popup:dict1];
            return;
        }
        [self refreshView];
        [tableView reloadData];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    // Return the number of rows in the section.
    return _pseudo.count;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *cellId = @"SubscriptionCell";
    SubscriptionCell *cell = [tableView dequeueReusableCellWithIdentifier:cellId forIndexPath:indexPath];
    // Configure the cell...
    
    int row = [indexPath row];
    cell.pseudo.text = _pseudo[row];
    return cell;
}

-(void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
   
    if ([[segue identifier] isEqualToString:@"ShowProfil"]){
     
        ProfilViewController *profilController = [segue destinationViewController];
        NSIndexPath *myIndexPath = [self.tableView indexPathForSelectedRow];
        
        int row = [myIndexPath row];
        profilController.ProfilModal = @[_pseudo[row], _idRow[row]];
    }

}

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    } else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath {
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
