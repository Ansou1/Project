//
//  PartitionSearchTableViewController.m
//  MSW
//
//  Created by simon on 15/12/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "PartitionSearchTableViewController.h"
#import "ApiMethod.h"
#import "ProfilViewController.h"
#import "SubscriptionCell.h"
#import "MusicSheetViewController.h"


@interface PartitionSearchTableViewController ()

@end

@implementation PartitionSearchTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.searchBarSheet.delegate = self;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) searchBarSearchButtonClicked:(UISearchBar *)searchBar{
    [self refreshView];
}

-(void) refreshView{
    ApiMethod *api = [[ApiMethod alloc]init];
    NSString *post1 = [NSString stringWithFormat:@"https://musicsheetwriter.tk/api/scores?title=%@", self.searchBarSheet.text];
    NSString *post = [post1 stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
    NSDictionary *dict1 = [api ApiMethodGetAfnetworkingAt:post];
    if (code_global != 200){
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
    [self.tableView reloadData];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    // Return the number of rows in the section.
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
        NSIndexPath *indexPath = [self.tableView indexPathForSelectedRow];
        musicSheet.musicSheetModal = @[self.titleMusicSheet[indexPath.row], self.urlMusicSheet[indexPath.row], self.userMusicSheet[indexPath.row], self.idRow[indexPath.row]];
    }
}

@end
