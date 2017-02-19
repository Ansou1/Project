//
//  ListSheetTableViewController.h
//  MSW
//
//  Created by Antoine Simon on 23/02/2016.
//  Copyright © 2016 Score Lab. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ListSheetTableViewController : UITableViewController
@property (strong, nonatomic) NSMutableArray *pseudo;
@property (strong, nonatomic) NSMutableArray *idRow;
@property (strong, nonatomic) NSMutableArray *result;
@property (strong, nonatomic) NSMutableArray *urlMusicSheet;
@property (strong, nonatomic) NSMutableArray *titleMusicSheet;
@property (strong, nonatomic) NSMutableArray *userMusicSheet;
@end
