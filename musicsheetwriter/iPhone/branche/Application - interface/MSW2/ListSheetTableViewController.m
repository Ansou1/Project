//
//  ListSheetTableViewController.m
//  MSW
//
//  Created by Antoine Simon on 23/02/2016.
//  Copyright © 2016 Score Lab. All rights reserved.
//

#import "ListSheetTableViewController.h"
#import "PartitionSearchTableViewController.h"
#import "ApiMethod.h"
#import "ProfilViewController.h"
#import "SubscriptionCell.h"
#import "MusicSheetViewController.h"

@interface ListSheetTableViewController ()

@end

@implementation ListSheetTableViewController

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
    NSString *post =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/scores/own", idProfil_global];
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
    return self.titleMusicSheet.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *cellId = @"SubscriptionCell";
    UITableViewCell *cell2 = [tableView dequeueReusableCellWithIdentifier:cellId forIndexPath:indexPath];
    cell2.textLabel.text = self.titleMusicSheet[indexPath.row];
    return cell2;
}

-(void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    if ([[segue identifier] isEqualToString:@"ShowPartition"]){
        MusicSheetViewController *musicSheet = [segue destinationViewController];
        NSIndexPath *myIndexPath = nil;
        myIndexPath = [self.tableView indexPathForSelectedRow];
        musicSheet.musicSheetModal = @[self.titleMusicSheet[myIndexPath.row], self.urlMusicSheet[myIndexPath.row], self.userMusicSheet[myIndexPath.row], self.idRow[myIndexPath.row]];
    }
}

@end
