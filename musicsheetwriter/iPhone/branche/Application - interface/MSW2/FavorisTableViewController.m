//
//  FavorisTableViewController.m
//  MSW
//
//  Created by simon on 15/12/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "FavorisTableViewController.h"
#import "ApiMethod.h"
#import "MusicSheetViewController.h"
#import "SubscriptionCell.h"
#import "GuestMode.h"

@interface FavorisTableViewController ()

@end

@implementation FavorisTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    //check if it is a guest mode
    GuestMode *guest = [[GuestMode alloc] init];
    if ([guest CheckIfTheUserIsAGuestId:self] == true) {
        return;
    }
    [self refreshView];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) refreshView{
    NSString *post =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/scores/favourites", Id_global];
    ApiMethod *api = [[ApiMethod alloc]init];
    NSDictionary *dict1 = [api ApiMethodGetAfnetworkingAt:post];
    if (code_global != 200)
    {
        [api popup:dict1 id:self];
        return;
    }
    _titleMusicSheet = [[NSMutableArray alloc] initWithCapacity:0];
    _idRow = [[NSMutableArray alloc] initWithCapacity:0];
    self.urlMusicSheet = [[NSMutableArray alloc] initWithCapacity:0];
    self.userMusicSheet = [[NSMutableArray alloc] initWithCapacity:0];
    
    for(NSDictionary *item in dict1) {
        [_titleMusicSheet addObject:[item valueForKey:@"title"]];
        [_idRow addObject:[item valueForKey:@"id"]];
        [self.urlMusicSheet addObject:[item valueForKey:@"location_preview"]];
    }
    NSDictionary *dict2;
    dict2 = [dict1 valueForKeyPath:@"by"];
    for(NSDictionary *item2 in dict2){
        [self.userMusicSheet addObject:[item2 valueForKey:@"username"]];
        //another line should be add there if the ID of the user is required
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _titleMusicSheet.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    // change this information to set the name of the partition
    static NSString *cellId = @"SubscriptionCell";
    SubscriptionCell *cell = [tableView dequeueReusableCellWithIdentifier:cellId forIndexPath:indexPath];
    cell.pseudo.text = self.titleMusicSheet[indexPath.row];
    return cell;
}

-(void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    if ([[segue identifier] isEqualToString:@"ShowPartition"]){
        MusicSheetViewController *musicSheet = [segue destinationViewController];
        NSIndexPath *myIndexPath = nil;
        myIndexPath = [self.tableView indexPathForSelectedRow];
        musicSheet.musicSheetModal = @[self.titleMusicSheet[myIndexPath.row], self.urlMusicSheet[myIndexPath.row], self.userMusicSheet[myIndexPath.row], self.idRow[myIndexPath.row]];
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
        NSString * post =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/scores/favourites/%@",Id_global , self.idRow[indexPath.row]];
        ApiMethod *api = [[ApiMethod alloc]init];
        NSDictionary *dict1 = [api ApiMethodDeleteAfnetworkingAt:post];
        if (code_global != 204){
            [api popup:dict1 id:self];
            return;
        }
        [self refreshView];
        [tableView reloadData];
    }
}

@end
