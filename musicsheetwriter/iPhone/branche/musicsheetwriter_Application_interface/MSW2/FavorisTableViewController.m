//
//  FavorisTableViewController.m
//  MSW
//
//  Created by simon on 15/12/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "FavorisTableViewController.h"
#import "ApiMethod.h"
//#import "" importer controler de vue partition pour envoyer des infos
#import "ProfilViewController.h"
//a enlever
#import "SubscriptionCell.h"
#import "GuestMode.h"

@interface FavorisTableViewController ()

@end

@implementation FavorisTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
    
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
    
    //check if it is a guest mode
    GuestMode *guest = [[GuestMode alloc] init];
    if ([guest CheckIfTheUserIsAGuest] == true) {
        //NSLog(@"test test test");
        return;
    }

    [self refreshView];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) refreshView{
    NSString *post =[NSString stringWithFormat:@"http://163.5.84.253/api/users/%@/scores/favourites", Id_global];
    ApiMethod *api = [[ApiMethod alloc]init];
    NSDictionary *dict1 = [api getMethodWithString:post];
    //NSLog(@"aze %@", dict1);
    if (code_global != 200)
    {
        [api popup:dict1];
        return;
    }
    _pseudo = [[NSMutableArray alloc] initWithCapacity:0];
    _idRow = [[NSMutableArray alloc] initWithCapacity:0];
    
    for(NSDictionary *item in dict1) {
        [_pseudo addObject:[item valueForKey:@"title"]];
        [_idRow addObject:[item valueForKey:@"id"]];
    }
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
#warning Potentially incomplete method implementation.
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
#warning Incomplete method implementation.
    // Return the number of rows in the section.
    return _pseudo.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *cellId = @"SubscriptionCell";
    SubscriptionCell *cell = [tableView dequeueReusableCellWithIdentifier:cellId forIndexPath:indexPath];
    
    // Configure the cell...
    
    // chnage this information to set the name of the partition
    int row = [indexPath row];
    cell.pseudo.text = _pseudo[row];
    
    return cell;
}

-(void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    
    /* this methode is used to send data an open the right profil
     if ([[segue identifier] isEqualToString:@"ShowProfil"]){
     
     ProfilViewController *profilController = [segue destinationViewController];
     NSIndexPath *myIndexPath = [self.tableView indexPathForSelectedRow];
     
     int row = [myIndexPath row];
     NSString * post =[NSString stringWithFormat:@"http://163.5.84.253/api/users"];
     ApiMethod *api = [[ApiMethod alloc]init];
     NSDictionary *dict1 = [api getMethodWithString:post];
     if (code_global != 200)
     {
     [api popup:dict1];
     return;
     }
     _idRow2 = [[NSMutableArray alloc] initWithCapacity:0];
     for(NSDictionary *item in dict1) {
     [_idRow2 addObject:[item valueForKey:@"id"]];
     }
     
     profilController.ProfilModal = @[_pseudo[row], _idRow[row], _idRow[row]];
     }*/
    [[segue identifier] isEqualToString:@"ShowPartition"];
}


/*
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:<#@"reuseIdentifier"#> forIndexPath:indexPath];
    
    // Configure the cell...
    
    return cell;
}
*/

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
